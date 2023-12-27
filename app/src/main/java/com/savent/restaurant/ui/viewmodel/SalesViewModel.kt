package com.savent.restaurant.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import com.savent.restaurant.*
import com.savent.restaurant.data.local.model.Printer
import com.savent.restaurant.data.common.model.Session
import com.savent.restaurant.data.repository.OrdersRepository
import com.savent.restaurant.data.repository.PrinterRepository
import com.savent.restaurant.data.repository.SessionRepository
import com.savent.restaurant.domain.usecase.GetSalesUseCase
import com.savent.restaurant.domain.usecase.IsGrantedAndroid12BLEPermissionUseCase
import com.savent.restaurant.domain.usecase.ShareReceiptByUseCase
import com.savent.restaurant.domain.usecase.ShareReceiptUseCase
import com.savent.restaurant.ui.model.SharedReceipt
import com.savent.restaurant.ui.screen.sales.SalesEvent
import com.savent.restaurant.ui.screen.sales.SalesState
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SalesViewModel(
    private val connectivityObserver: NetworkConnectivityObserver,
    private val sessionRepository: SessionRepository,
    private val ordersRepository: OrdersRepository,
    private val getSalesUseCase: GetSalesUseCase,
    private val sharedReceiptUseCase: ShareReceiptUseCase,
    private val printerRepository: PrinterRepository,
    private val isGrantedAndroid12BLEPermissionUseCase: IsGrantedAndroid12BLEPermissionUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SalesState())
    val state = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var session: Session? = null
    private var _networkStatus = ConnectivityObserver.Status.Available
    private var printing: Printing? = null

    private var networkObserverJob: Job? = null
    private var getSessionJob: Job? = null
    private var getShareReceiptMethods: Job? = null
    private var getSalesJob: Job? = null
    private var reloadSalesJob: Job? = null
    private var getReceiptToSend: Job? = null
    private var printReceiptJob: Job? = null
    private var printerJob: Job? = null

    sealed class UiEvent {
        class ShowMessage(val message: Message) : UiEvent()
        class ShareReceipt(val sharedReceipt: SharedReceipt) : UiEvent()
        class ScanPrinters(val orderId: Int) : UiEvent()
    }

    init {
        observeNetworkChange()
        loadSession()
        getShareReceiptMethods()
        getSales()
    }

    fun onEvent(event: SalesEvent) {
        when (event) {
            is SalesEvent.Search -> getSales(event.query)
            is SalesEvent.ShareReceipt -> {
                when (event.method) {
                    SharedReceipt.Method.Printer -> printReceipt(event.orderId)
                    else -> getReceiptToSend(event.orderId, event.method)
                }
            }
            SalesEvent.RemovePrintDevice -> removeCurrentPrinter()

        }
    }

    private fun observeNetworkChange() {
        networkObserverJob?.cancel()
        networkObserverJob = viewModelScope.launch(Dispatchers.IO) {
            connectivityObserver.observe().collectLatest { status ->
                _networkStatus = status
            }
        }

    }


    private fun loadSession() {
        getSessionJob?.cancel()
        getSessionJob = viewModelScope.launch(Dispatchers.IO) {
            when (val result = sessionRepository.getSession()) {
                is Resource.Error -> {
                    _uiEvent.emit(UiEvent.ShowMessage(result.message))
                }
                is Resource.Success -> session = result.data
            }
        }
    }

    private fun getShareReceiptMethods() {
        getShareReceiptMethods?.cancel()
        getShareReceiptMethods = viewModelScope.launch(Dispatchers.IO) {
            val methods = ShareReceiptByUseCase.getList()
            _state.update { it.copy(shareReceiptMethods = methods) }
        }
    }

    fun reloadSales() {
        reloadSalesJob?.cancel()
        reloadSalesJob = viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }
            if (!isNetworkAvailable()) {
                _state.update { it.copy(isLoading = false) }
                return@launch
            }
            session?.let { session ->
                val result = ordersRepository.fetchOrders(
                    restaurantId = session.restaurantId,
                    companyId = session.companyId
                )
                _state.update { it.copy(isLoading = false) }
                if (result is Resource.Error)
                    _uiEvent.emit(UiEvent.ShowMessage(result.message))
            }

        }
    }

    private fun getSales(query: String = "") {
        getSalesJob?.cancel()
        getSalesJob = viewModelScope.launch(Dispatchers.IO) {
            getSalesUseCase(query).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        _state.update { it.copy(sales = listOf()) }
                        _uiEvent.emit(UiEvent.ShowMessage(result.message))
                    }
                    is Resource.Success -> _state.update {
                        _state.value.copy(
                            sales = result.data
                        )
                    }
                }
            }
        }
    }

    fun printReceipt(orderId: Int) {
        printReceiptJob?.cancel()
        printReceiptJob = viewModelScope.launch(Dispatchers.IO) {
            val printer = printerRepository.getPrinter(Printer.Location.DiningRoom)
            if (printer == null || !isGrantedAndroid12BLEPermissionUseCase()) {
                _uiEvent.emit(UiEvent.ScanPrinters(orderId))
                return@launch
            }
            when (val result = sharedReceiptUseCase.getReceiptToPrintUseCase(orderId)) {
                is Resource.Error ->
                    _uiEvent.emit(UiEvent.ShowMessage(result.message))
                is Resource.Success -> {
                    if (printer.address != Printooth.getPairedPrinter()?.address)
                        Printooth.setPrinter(name = printer.name, address = printer.address)

                    printing = Printooth.printer()
                    printing?.printingCallback = object : PrintingCallback {
                        override fun connectingWithPrinter() {
                        }

                        override fun connectionFailed(error: String) {
                            viewModelScope.launch {
                                _uiEvent.emit(
                                    UiEvent.ShowMessage(
                                        Message.StringResource(R.string.printer_connection_failed)
                                    )
                                )
                                printerRepository.deletePrinter(Printer.Location.DiningRoom)
                            }

                        }

                        override fun onError(error: String) {
                            viewModelScope.launch {
                                _uiEvent.emit(
                                    UiEvent.ShowMessage(
                                        Message.StringResource(R.string.printer_error)
                                    )
                                )
                                printerRepository.deletePrinter(Printer.Location.DiningRoom)
                            }
                        }

                        override fun onMessage(message: String) {

                        }

                        override fun printingOrderSentSuccessfully() {
                            viewModelScope.launch {
                                _uiEvent.emit(
                                    UiEvent.ShowMessage(
                                        Message.StringResource(R.string.printer_order_sent_successfully)
                                    )
                                )
                            }
                        }

                        override fun disconnected() {
                        }

                    }

                    if (Printooth.hasPairedPrinter()) Printooth.printer().print(result.data)
                    else _uiEvent.emit(UiEvent.ScanPrinters(orderId))
                }
            }
        }
    }

    fun saveCurrentPrinter(onFinished: () -> Unit) {
        printerJob?.cancel()
        printerJob = viewModelScope.launch(Dispatchers.IO) {
            val pairedPrinter = Printooth.getPairedPrinter() ?: return@launch
            val printer = Printer(
                name = pairedPrinter.name ?: return@launch,
                address = pairedPrinter.address,
                location = Printer.Location.DiningRoom
            )

            printerRepository.savePrinter(printer)
            onFinished()
        }
    }

    private fun removeCurrentPrinter() {
        printerJob?.cancel()
        printerJob = viewModelScope.launch(Dispatchers.IO) {
            Printooth.removeCurrentPrinter()
            printerRepository.deletePrinter(Printer.Location.DiningRoom)
        }
    }

    private fun getReceiptToSend(orderId: Int, method: SharedReceipt.Method) {
        getReceiptToSend?.cancel()
        getReceiptToSend = viewModelScope.launch(Dispatchers.IO) {
            when (val result = sharedReceiptUseCase.getReceiptToSendUseCase(orderId, method)) {
                is Resource.Error ->
                    _uiEvent.emit(UiEvent.ShowMessage(result.message))
                is Resource.Success ->
                    _uiEvent.emit(UiEvent.ShareReceipt(result.data))
            }
        }
    }


    private suspend fun isNetworkAvailable(): Boolean {
        if (_networkStatus != ConnectivityObserver.Status.Available) {
            _uiEvent.emit(
                UiEvent.ShowMessage(Message.StringResource(R.string.internet_error))
            )
            return false
        }
        return true
    }
}