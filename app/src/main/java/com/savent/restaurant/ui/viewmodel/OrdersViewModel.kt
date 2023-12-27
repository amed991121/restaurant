package com.savent.restaurant.ui.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import com.savent.restaurant.ConnectivityObserver
import com.savent.restaurant.NetworkConnectivityObserver
import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.*
import com.savent.restaurant.data.local.model.KitchenNote
import com.savent.restaurant.data.local.model.OrderEntity
import com.savent.restaurant.data.local.model.Printer
import com.savent.restaurant.data.repository.*
import com.savent.restaurant.domain.usecase.*
import com.savent.restaurant.ui.model.OrderModel
import com.savent.restaurant.ui.model.dish.DishModel
import com.savent.restaurant.ui.model.dish.toModel
import com.savent.restaurant.ui.screen.orders.OrdersEvent
import com.savent.restaurant.ui.screen.orders.OrdersState
import com.savent.restaurant.utils.DateTimeObj
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.NameFormat
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val connectivityObserver: NetworkConnectivityObserver,
    private val sessionRepository: SessionRepository,
    private val tablesRepository: TablesRepository,
    private val getOpenOrdersUseCase: GetOpenOrdersUseCase,
    private val dinersRepository: DinersRepository,
    private val dishesRepository: DishesRepository,
    private val getMenuCategoriesWithIconUseCase: GetMenuCategoriesWithIconUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val tableHasOrderUseCase: TableHasOrderUseCase,
    private val ordersRepository: OrdersRepository,
    private val addDishUnitUseCase: AddDishUnitUseCase,
    private val removeDishUnitUseCase: RemoveDishUnitUseCase,
    private val computeOrderUseCase: ComputeOrderUseCase,
    private val getOpenOrderUseCase: GetOpenOrderUseCase,
    private val getDishesPendingSendToKitchenUseCase: GePendingDishesSendToKitchenUseCase,
    private val getKitchenNoteUseCase: GetKitchenNoteUseCase,
    private val kitchenNotesRepository: KitchenNotesRepository,
    private val printerRepository: PrinterRepository,
    private val isGrantedAndroid12BLEPermissionUseCase: IsGrantedAndroid12BLEPermissionUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(OrdersState())
    val state = _state.asStateFlow()

    private val _message = MutableSharedFlow<Message>()
    val message = _message.asSharedFlow()

    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()

    sealed class Event {
        object OrderNotFound: Event()
        object OrderCreated : Event()
        object OrderRegistered : Event()
        object ScanPrinterDevices : Event()
    }

    private var session: Session? = null
    private var order = OrderEntity()
    private var printing: Printing? = null
    private var _networkStatus = ConnectivityObserver.Status.Available

    private var refreshDataJob: Job? = null
    private var networkObserverJob: Job? = null
    private var getSessionJob: Job? = null
    private var getMenuCategoriesJob: Job? = null
    private var getPaymentMethodsJob: Job? = null
    private var getTablesJob: Job? = null
    private var getDinersJob: Job? = null
    private var getDishesJob: Job? = null
    private var getOpenOrdersJob: Job? = null
    private var createOrderJob: Job? = null
    private var getOrderJob: Job? = null
    private var deleteOrderJob: Job? = null
    private var computeOrderJob: Job? = null
    private var registerOrderJob: Job? = null
    private var kitchenNoteJob: Job? = null
    private var printNoteJob: Job? = null
    private var printerJob: Job? = null

    init {
        loadSession()
        getMenuCategories()
        getPaymentMethods()
        observeNetworkChange()
        getOpenOrders()
        getTables()
        getDiners()
        getDishes()
        refreshData()
        savedStateHandle.get<Int>("orderId").let {
            if(it == null) viewModelScope.launch { _event.emit(Event.OrderNotFound) }
            else getCurrentOrder(it)
        }
    }

    fun onEvent(event: OrdersEvent) {
        when (event) {
            is OrdersEvent.SearchOrders -> getOpenOrders(event.query)
            is OrdersEvent.SelectOrder -> {
                getCurrentOrder(event.orderId)
                getPendingDishesSendToKitchen(event.orderId)
            }
            is OrdersEvent.AddOrderTag -> addOrderTag(event.tag, event.isPersistent)
            OrdersEvent.CreateOrder -> createOrder()
            is OrdersEvent.SearchDiners -> getDiners(event.query)
            is OrdersEvent.SearchTables -> getTables(event.query)
            is OrdersEvent.SelectDiner -> selectDiner(event.dinerId)
            is OrdersEvent.SelectTable -> selectTable(event.tableId)
            is OrdersEvent.AddDishUnit -> {
                if (event.isPersistent) addDishUnitPersistently(event.dishId)
                else addDishUnit(event.dishId)
            }
            is OrdersEvent.RemoveDishUnit -> {
                if (event.isPersistent) removeDishUnitPersistently(event.dishId)
                else removeDishUnit(event.dishId)
            }
            is OrdersEvent.SearchDishes -> {
                getDishes(event.query, event.category)
                getMenuCategories(event.category)
            }
            is OrdersEvent.DeleteOrder -> deleteCurrentOrder()
            is OrdersEvent.UpdateCollectedValues -> {
                order = order.copy(
                    discounts = event.discounts,
                    collected = event.collected
                )
                recomputeOrder()
            }
            OrdersEvent.ResetOrder -> resetOrder()
            is OrdersEvent.SelectPaymentMethod -> selectPaymentMethod(event.method)
            OrdersEvent.RegisterOrder -> registerOrder()
            OrdersEvent.SelectPrinter -> saveCurrentPrinter()
            is OrdersEvent.SendKitchenOrderToPrinter -> sendNoteToPrinter(event.priorities)
            OrdersEvent.ScanPrinterDevices ->
                viewModelScope.launch { _event.emit(Event.ScanPrinterDevices) }
        }
    }

    private fun resetOrder() {
        viewModelScope.launch {
            order = OrderEntity()
            _state.update {
                it.copy(
                    order = OrderModel(),
                    dishes = it.dishes.map { it1 ->
                        it1.copy(units = 0)
                    })
            }
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

    fun refreshData() {
        refreshDataJob?.cancel()
        refreshDataJob = viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }
            if (isNetworkAvailable()) {
                reloadTables()
                reloadDiners()
                reloadDishes()
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun loadSession() {
        getSessionJob?.cancel()
        getSessionJob = viewModelScope.launch(Dispatchers.IO) {
            when (val result = sessionRepository.getSession()) {
                is Resource.Error -> {
                    _message.emit(result.message)
                }
                is Resource.Success -> session = result.data
            }
        }
    }

    private fun getMenuCategories(typeSelected: Dish.Category = Dish.Category.ALL) {
        getMenuCategoriesJob?.cancel()
        getMenuCategoriesJob = viewModelScope.launch(Dispatchers.IO) {
            val categories = getMenuCategoriesWithIconUseCase(typeSelected)
            _state.update { it.copy(menuCategories = categories) }
        }
    }

    private fun getPaymentMethods() {
        getPaymentMethodsJob?.cancel()
        getPaymentMethodsJob = viewModelScope.launch(Dispatchers.IO) {
            val methods = PaymentMethodUseCase.getList()
            _state.update { it.copy(paymentMethods = methods) }
        }
    }

    private fun selectPaymentMethod(method: PaymentMethod) {
        viewModelScope.launch(Dispatchers.IO) {
            order = order.copy(paymentMethod = method)
            when (val result = ordersRepository.updateOrder(order)) {
                is Resource.Error -> _message.emit(result.message)
                else -> {}
            }
        }

    }

    private suspend fun reloadTables() {
        session?.let {
            val result = tablesRepository.fetchTables(
                restaurantId = it.restaurantId,
                companyId = it.companyId
            )
            if (result is Resource.Error) _message.emit(result.message)
        }
    }

    private fun getTables(query: String = "") {
        getTablesJob?.cancel()
        getTablesJob = viewModelScope.launch(Dispatchers.IO) {
            tablesRepository.getAllTables(query).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        _state.update { it.copy(tables = listOf()) }
                        _message.emit(result.message)
                    }
                    is Resource.Success -> _state.update {
                        it.copy(
                            tables = result.data.map { it1 ->
                                it1.copy(name = NameFormat.format(it1.name))
                            }
                        )
                    }
                }
            }
        }
    }

    private suspend fun reloadDiners() {
        session?.let {
            val result = dinersRepository.fetchDiners(
                restaurantId = it.restaurantId,
                companyId = it.companyId
            )
            if (result is Resource.Error) _message.emit(result.message)
        }
    }

    private fun getDiners(query: String = "") {
        getDinersJob?.cancel()
        getDinersJob = viewModelScope.launch(Dispatchers.IO) {
            dinersRepository.getAllDiners(query).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        _state.update { it.copy(diners = listOf()) }
                        _message.emit(result.message)
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                diners = result.data.map { it1 ->
                                    it1.copy(name = NameFormat.format(it1.name))
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun reloadDishes() {
        session?.let {
            val result = dishesRepository.fetchDishes(
                restaurantId = it.restaurantId,
                companyId = it.companyId
            )
            if (result is Resource.Error) _message.emit(result.message)
        }
    }

    private fun getDishes(query: String = "", category: Dish.Category = Dish.Category.ALL) {
        getDishesJob?.cancel()
        getDishesJob = viewModelScope.launch(Dispatchers.IO) {
            dishesRepository.getAllDishes(query, category).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        _state.update { it.copy(dishes = listOf()) }
                        _message.emit(result.message)
                    }
                    is Resource.Success -> _state.update {
                        it.copy(
                            dishes = result.data.map { it1 ->
                                var dish = it1.toModel()
                                kotlin.run breaking@{
                                    order.dishes.keys.forEach { dishId ->
                                        if (dishId == dish.id)
                                            dish = dish.copy(units = order.dishes[dishId] ?: 0)
                                    }
                                }
                                dish
                            }
                        )
                    }
                }
            }
        }
    }

    private fun addOrderTag(tag: String, isPersistent: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(order = it.order.copy(tag = tag)) }
            order = order.copy(tag = tag)
            if (isPersistent) ordersRepository.updateOrder(order)
        }
    }

    private fun selectDiner(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            var diner: Diner? = null
            when (val result = dinersRepository.getDiner(id)) {
                is Resource.Error -> {
                    _message.emit(result.message)
                    return@launch
                }
                is Resource.Success -> diner = result.data
            }

            _state.update {
                it.copy(order = it.order.copy(tag = NameFormat.format(diner.name)))
            }
            order = order.copy(dinerId = id, tag = diner.name)
            ordersRepository.updateOrder(order)
        }
    }

    private fun selectTable(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            var table: Table? = null
            when (val result = tablesRepository.getTable(id)) {
                is Resource.Error -> {
                    _message.emit(result.message)
                    return@launch
                }
                is Resource.Success -> table = result.data
            }

            _state.update {
                it.copy(order = it.order.copy(tableName = table.name))
            }
            order = order.copy(tableId = id)
        }
    }

    private fun addDishUnit(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            var dish: Dish? = null
            when (val result = dishesRepository.getDish(id)) {
                is Resource.Error -> {
                    _message.emit(result.message)
                    return@launch
                }
                is Resource.Success -> dish = result.data
            }

            _state.getAndUpdate { state ->
                val allDishes = mutableListOf<DishModel>()
                state.dishes.forEach {
                    if (it.id == id) {
                        allDishes.add(it.copy(units = it.units + 1))
                    } else
                        allDishes.add(it)
                }

                val orderDishes = mutableListOf<DishModel>()
                var exists = false

                state.order.dishes.forEach {
                    if (it.id == id) {
                        exists = true
                        orderDishes.add(it.copy(units = it.units + 1))

                    } else
                        orderDishes.add(it)
                }

                if (!exists) orderDishes.add(dish.toModel().copy(units = 1))
                state.copy(
                    dishes = allDishes,
                    order = state.order.copy(dishes = orderDishes)
                )
            }

            val dishesMap = order.dishes
            dishesMap[id] = dishesMap.getOrDefault(id, 0) + 1
            order = order.copy(dishes = dishesMap)

        }
    }

    private fun removeDishUnit(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val allDishes = mutableListOf<DishModel>()

            _state.getAndUpdate { state ->
                state.dishes.forEach {
                    if (it.id == id) {
                        allDishes.add(
                            it.copy(
                                units = (it.units - 1).let { units ->
                                    if (units < 0) 0 else units
                                })
                        )
                    } else
                        allDishes.add(it)
                }

                val orderDishes = mutableListOf<DishModel>()
                state.order.dishes.forEach {
                    if (it.id == id) {
                        (it.units - 1).let { units ->
                            if (units < 1) return@forEach
                            else orderDishes.add(it.copy(units = units))
                        }

                    } else
                        orderDishes.add(it)
                }

                state.copy(
                    dishes = allDishes,
                    order = state.order.copy(dishes = orderDishes)
                )
            }

            val dishesMap = order.dishes
            val units = dishesMap.getOrDefault(id, 1) - 1
            if (units > 0) dishesMap[id] = units
            else dishesMap.remove(id)
            order = order.copy(dishes = dishesMap)

        }
    }

    private fun addDishUnitPersistently(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result =
                addDishUnitUseCase(dishId = id, orderId = order.id)) {
                is Resource.Error -> {
                    _message.emit(result.message)
                }
                else -> {}
            }
        }
    }

    private fun removeDishUnitPersistently(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result =
                removeDishUnitUseCase(dishId = id, orderId = order.id)) {
                is Resource.Error -> {
                    _message.emit(result.message)
                }
                else -> {}
            }
        }
    }

    private fun recomputeOrder() {
        computeOrderJob?.cancel()
        computeOrderJob = viewModelScope.launch(Dispatchers.IO) {
            when (val result = computeOrderUseCase(order)) {
                is Resource.Error -> {
                    _message.emit(result.message)
                }
                else -> {}
            }
        }
    }

    private fun getOpenOrders(query: String = "") {
        getOpenOrdersJob?.cancel()
        getOpenOrdersJob = viewModelScope.launch(Dispatchers.IO) {
            getOpenOrdersUseCase(query).collectLatest { result ->

                when (result) {
                    is Resource.Error -> {
                        _state.update { it.copy(orders = listOf()) }
                        _message.emit(result.message)
                    }
                    is Resource.Success -> {
                        //Log.d("log_","openorders${result.data}")
                        _state.update {
                            it.copy(
                                orders = result.data
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getCurrentOrder(id: Int) {
        getOrderJob?.cancel()
        getOrderJob = viewModelScope.launch(Dispatchers.IO) {
            savedStateHandle["orderId"] = id
            launch {
                ordersRepository.getOrderAsync(id).onEach { result ->
                    when (result) {
                        is Resource.Error -> {
                            _message.emit(result.message)
                        }
                        is Resource.Success -> {
                            order = result.data
                        }
                    }
                }.collect()
            }
            launch {
                getOpenOrderUseCase(id).onEach { result ->
                    when (result) {
                        is Resource.Error -> {
                            _message.emit(result.message)
                        }
                        is Resource.Success -> {
                            val dishes = _state.value.dishes.map {
                                var dish = it
                                kotlin.run breaking@{
                                    result.data.dishes.forEach { orderDish ->
                                        if (it.id == orderDish.id) {
                                            dish =
                                                dish.copy(units = orderDish.units)
                                            return@breaking
                                        }

                                    }
                                    dish = dish.copy(units = 0)
                                }
                                dish
                            }

                            _state.update {
                                it.copy(
                                    order = result.data,
                                    dishes = dishes,
                                )
                            }
                        }
                    }
                }.collect()
            }
        }
    }

    private fun getPendingDishesSendToKitchen(orderId: Int) {
        kitchenNoteJob?.cancel()
        kitchenNoteJob = viewModelScope.launch(Dispatchers.IO) {
            getDishesPendingSendToKitchenUseCase(orderId).collectLatest { result ->
                when (result) {
                    is Resource.Error -> _message.emit(result.message)
                    is Resource.Success -> {
                        _state.update {
                            it.copy(order = it.order.copy(pendingKitchenDishes = result.data))
                        }
                    }
                }
            }
        }
    }

    private fun sendNoteToPrinter(priorities: HashMap<Int, Int>) {
        printNoteJob?.cancel()
        printNoteJob = viewModelScope.launch(Dispatchers.IO) {
            val printer = printerRepository.getPrinter(Printer.Location.Kitchen)
            if (printer == null || !isGrantedAndroid12BLEPermissionUseCase()) {
                _message.emit(Message.StringResource(R.string.printer_requeried))
                return@launch
            }
            when (val result = getKitchenNoteUseCase(orderId = order.id, priorities = priorities)) {
                is Resource.Error ->
                    _message.emit(result.message)
                is Resource.Success -> {
                    if (printer.address != Printooth.getPairedPrinter()?.address)
                        Printooth.setPrinter(name = printer.name, address = printer.address)

                    printing = Printooth.printer()
                    printing?.printingCallback = object : PrintingCallback {
                        override fun connectingWithPrinter() {
                        }

                        override fun connectionFailed(error: String) {
                            viewModelScope.launch {
                                _message.emit(
                                    Message.StringResource(R.string.printer_connection_failed)
                                )
                                printerRepository.deletePrinter(location = Printer.Location.Kitchen)
                            }

                        }

                        override fun onError(error: String) {
                            viewModelScope.launch {
                                _message.emit(
                                    Message.StringResource(R.string.printer_error)
                                )
                                printerRepository.deletePrinter(location = Printer.Location.Kitchen)
                            }
                        }

                        override fun onMessage(message: String) {

                        }

                        override fun printingOrderSentSuccessfully() {
                            viewModelScope.launch {
                                _message.emit(
                                    Message.StringResource(R.string.printer_order_sent_successfully)
                                )
                                kitchenNotesRepository.upsertNote(
                                    KitchenNote(
                                        orderId = order.id,
                                        dishesSent = order.dishes
                                    )
                                )
                            }

                        }

                        override fun disconnected() {
                        }

                    }
                    //Log.d("log_", Gson().toJson(result.data))
                    if (Printooth.hasPairedPrinter()) Printooth.printer().print(result.data)
                    else _message.emit(Message.StringResource(R.string.printer_requeried))
                    
                }
            }
        }
    }

    fun saveCurrentPrinter() {
        printerJob?.cancel()
        printerJob = viewModelScope.launch(Dispatchers.IO) {
            val pairedPrinter = Printooth.getPairedPrinter() ?: return@launch
            val printer = Printer(
                name = pairedPrinter.name ?: return@launch,
                address = pairedPrinter.address,
                location = Printer.Location.Kitchen
            )

            printerRepository.savePrinter(printer)
        }
    }

    private fun deleteCurrentOrder() {
        deleteOrderJob?.cancel()
        deleteOrderJob = viewModelScope.launch(Dispatchers.IO) {
            when (val result = ordersRepository.deleteOrder(order.id)) {
                is Resource.Error -> {
                    _message.emit(result.message)
                }
                else -> {
                    _message.emit(Message.StringResource(R.string.order_deleted))
                    getOrderJob?.cancel()
                }
            }
        }
    }

    private fun createOrder() {
        createOrderJob?.cancel()
        createOrderJob = viewModelScope.launch(Dispatchers.IO) {
            if (order.tableId == 0) {
                _message.emit(Message.StringResource(R.string.table_is_required))
                return@launch
            }
            if (order.tag.isNullOrEmpty())
                when (val result = tableHasOrderUseCase(order.tableId)) {
                    is Resource.Error -> {
                        _message.emit(result.message)
                        return@launch
                    }
                    is Resource.Success -> {
                        if (result.data) {
                            _message.emit(Message.StringResource(R.string.tag_is_required))
                            return@launch
                        }
                    }
                }

            when (val result = createOrderUseCase(
                tableId = order.tableId,
                dishes = order.dishes,
                dinerId = order.dinerId,
                tag = order.tag
            )) {
                is Resource.Error -> {
                    _message.emit(result.message)
                }
                is Resource.Success -> {
                    _event.emit(Event.OrderCreated)
                }
            }


        }
    }

    private fun registerOrder() {
        registerOrderJob?.cancel()
        registerOrderJob = viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }
            if (order.tag.isEmpty())
                when (val result = tableHasOrderUseCase(order.tableId)) {
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false) }
                        _message.emit(result.message)
                        return@launch
                    }
                    is Resource.Success -> {
                        if (result.data) {
                            _state.update { it.copy(isLoading = false) }
                            _message.emit(Message.StringResource(R.string.tag_is_required))
                            return@launch
                        }
                    }
                }
            if (order.dishes.size == 0) {
                _state.update { it.copy(isLoading = false) }
                _message.emit(Message.StringResource(R.string.at_least_one_dish))
                return@launch
            }
            if (order.collected < order.total) {
                _state.update { it.copy(isLoading = false) }
                _message.emit(Message.StringResource(R.string.diner_required_for_credit_sale))
                return@launch
            }
            if (!isNetworkAvailable()) {
                _state.update { it.copy(isLoading = false) }
                return@launch
            }
            session?.let {
                ordersRepository.updateOrder(
                    order.copy(
                        date_timestamp = DateTimeObj.fromLong(System.currentTimeMillis())
                    )
                )
                when (val result = ordersRepository.saveOrder(
                    localId = order.id,
                    restaurantId = it.restaurantId,
                    companyId = it.companyId,
                    employeeId = it.employeeId,
                    branchId = it.branchId
                )) {
                    is Resource.Error -> {
                        _state.update { it1 -> it1.copy(isLoading = false) }
                        _message.emit(result.message)
                    }
                    is Resource.Success -> {
                        kitchenNotesRepository.deleteNote(order.id)
                        getOrderJob?.cancel()
                        _state.update { it1 -> it1.copy(isLoading = false) }
                        _event.emit(Event.OrderRegistered)
                    }
                }
            } ?: kotlin.run {
                _state.update { it.copy(isLoading = false) }
                _message.emit(Message.StringResource(R.string.session_not_found))
            }
        }
    }

    private suspend fun isNetworkAvailable(): Boolean {
        if (_networkStatus != ConnectivityObserver.Status.Available) {
            _message.emit(Message.StringResource(R.string.internet_error))
            return false
        }
        return true
    }

}