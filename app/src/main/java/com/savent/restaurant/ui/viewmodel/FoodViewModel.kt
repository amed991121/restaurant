package com.savent.restaurant.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.Coil
import coil.ImageLoader
import com.google.gson.Gson
import com.savent.restaurant.*
import com.savent.restaurant.data.common.model.Dish
import com.savent.restaurant.data.common.model.Session
import com.savent.restaurant.data.repository.DishesRepository
import com.savent.restaurant.data.repository.SessionRepository
import com.savent.restaurant.data.repository.TablesRepository
import com.savent.restaurant.domain.usecase.AddDishToOrderUseCase
import com.savent.restaurant.domain.usecase.GetMenuCategoriesWithIconUseCase
import com.savent.restaurant.domain.usecase.GetMenuCategoriesWithImageUseCase
import com.savent.restaurant.domain.usecase.GetTablesAndOrdersUseCase
import com.savent.restaurant.ui.model.dish.toModel
import com.savent.restaurant.ui.model.table_and_order.OrderNameModel
import com.savent.restaurant.ui.screen.food.DishToOrder
import com.savent.restaurant.ui.screen.food.FoodEvent
import com.savent.restaurant.ui.screen.food.FoodState
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.NameFormat
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject

class FoodViewModel(
    private val connectivityObserver: NetworkConnectivityObserver,
    private val sessionRepository: SessionRepository,
    private val dishesRepository: DishesRepository,
    private val tablesRepository: TablesRepository,
    private val getMenuCategoriesWithImageUseCase: GetMenuCategoriesWithImageUseCase,
    private val getMenuCategoriesWithIconUseCase: GetMenuCategoriesWithIconUseCase,
    private val getTablesAndOrdersUseCase: GetTablesAndOrdersUseCase,
    private val addDishToOrderUseCase: AddDishToOrderUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(FoodState())
    val state = _state.asStateFlow()

    private val _message = MutableSharedFlow<Message>()
    val message = _message.asSharedFlow()

    private var dishToOrder = DishToOrder()
    private var session: Session? = null
    private var _networkStatus = ConnectivityObserver.Status.Available

    private var refreshDataJob: Job? = null
    private var networkObserverJob: Job? = null
    private var getSessionJob: Job? = null
    private var getMenuCategoriesJob: Job? = null
    private var getDishesJob: Job? = null
    private var getTablesAndOrdersJob: Job? = null
    private var addDishToOrderJob: Job? = null


    init {
        loadSession()
        getMenuCategories()
        observeNetworkChange()
        refreshData()
    }

    fun onEvent(event: FoodEvent) {
        when (event) {
            is FoodEvent.DishToCurrentOrder -> {
                val ordersIdList = dishToOrder.ordersIdList.toMutableList()
                if (event.isAdded) ordersIdList.add(event.orderId)
                else ordersIdList.remove(event.orderId)
                dishToOrder = dishToOrder.copy(ordersIdList = ordersIdList)
                getTablesAndOrders()
            }
            is FoodEvent.DishToTable -> {
                val tablesIdList = dishToOrder.tablesIdList.toMutableList()
                if (event.isAdded) tablesIdList.add(event.tableId)
                else tablesIdList.remove(event.tableId)
                dishToOrder = dishToOrder.copy(tablesIdList = tablesIdList)
                getTablesAndOrders()
            }

            FoodEvent.SaveDishOrder -> addDishToOrder()
            is FoodEvent.SearchDishes -> {
                getDishes(event.query, event.category)
                getMenuCategories(event.category)
            }
            is FoodEvent.SelectDish -> {
                dishToOrder = DishToOrder(dishId = event.id)
                getTablesAndOrders()
            }
            is FoodEvent.SearchTablesAndOrders -> getTablesAndOrders(event.query)
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
            _state.update{it.copy(isLoading = true)}
            if (isNetworkAvailable()) {
                reloadDishes()
                reloadTables()
            }
            _state.update{it.copy(isLoading = false)}
        }
    }

    private fun loadSession() {
        getSessionJob?.cancel()
        getSessionJob = viewModelScope.launch(Dispatchers.IO) {
            when (val result = sessionRepository.getSession()) {
                is Resource.Error -> {
                    _message.emit(result.message)
                }
                is Resource.Success -> {
                    _state.update{it.copy(employeeName = NameFormat.format(result.data.employeeName))}
                    session = result.data
                }
            }
        }
    }

    private fun getMenuCategories(typeSelected: Dish.Category = Dish.Category.ALL) {
        getMenuCategoriesJob?.cancel()
        getMenuCategoriesJob = viewModelScope.launch(Dispatchers.IO) {
            val categoriesListWithImage = getMenuCategoriesWithImageUseCase()
            val categoriesListWithIcon = getMenuCategoriesWithIconUseCase(typeSelected)
            _state.update {
                it.copy(
                    categoriesListWithIcon = categoriesListWithIcon,
                    categoriesListWithImage = categoriesListWithImage
                )
            }
        }
    }


    private fun getDishes(query: String = "", category: Dish.Category = Dish.Category.ALL) {
        getDishesJob?.cancel()
        getDishesJob = viewModelScope.launch(Dispatchers.IO) {
            dishesRepository.getAllDishes(query, category).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        _state.update{it.copy(dishes = listOf())}
                        _message.emit(result.message)
                    }
                    is Resource.Success -> {
                        _state.update{it.copy(
                            dishes = result.data.map { it.toModel() }
                        )}
                    }
                }
            }
        }
    }

    private suspend fun reloadDishes() {
        session?.let {
            val imageLoader by inject<ImageLoader>(ImageLoader::class.java)
            imageLoader.memoryCache.clear()
            Coil.setImageLoader(imageLoader)
            val result = dishesRepository.fetchDishes(
                restaurantId = it.restaurantId,
                companyId = it.companyId
            )
            if (result is Resource.Error) _message.emit(result.message)
        }
    }

    private fun getTablesAndOrders(query: String = "") {
        getTablesAndOrdersJob?.cancel()
        getTablesAndOrdersJob = viewModelScope.launch(Dispatchers.Default) {
            getTablesAndOrdersUseCase(query).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        _state.update{it.copy(tableAndOrders = listOf())}
                        _message.emit(result.message)
                    }
                    is Resource.Success -> _state.update {
                        it.copy(
                            tableAndOrders = result.data.map {it1->
                                var model = it1
                                if (dishToOrder.tablesIdList.contains(it1.tableId)) {
                                    model = model.copy(isSelected = true)
                                }
                                val orderNames = mutableListOf<OrderNameModel>()
                                it1.orderNames.forEach { orderName ->
                                    if (dishToOrder.ordersIdList.contains(orderName.id))
                                        orderNames.add(orderName.copy(isSelected = true))
                                    else orderNames.add(orderName)
                                }
                                model = model.copy(orderNames = orderNames)
                                model
                            }
                        )
                    }
                }
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

    private fun addDishToOrder() {
        addDishToOrderJob?.cancel()
        addDishToOrderJob = viewModelScope.launch(Dispatchers.IO) {
            when (val result = addDishToOrderUseCase(
                dishId = dishToOrder.dishId,
                tablesIdList = dishToOrder.tablesIdList,
                ordersIdList = dishToOrder.ordersIdList
            )) {
                is Resource.Error -> _message.emit(result.message)
                is Resource.Success -> {}
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