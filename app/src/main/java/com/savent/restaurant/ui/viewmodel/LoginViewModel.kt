package com.savent.restaurant.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savent.restaurant.ConnectivityObserver
import com.savent.restaurant.NetworkConnectivityObserver
import com.savent.restaurant.R
import com.savent.restaurant.data.repository.CompaniesRepository
import com.savent.restaurant.data.repository.RestaurantsRepository
import com.savent.restaurant.data.repository.SessionRepository
import com.savent.restaurant.ui.screen.login.Credentials
import com.savent.restaurant.ui.screen.login.LoginEvent
import com.savent.restaurant.ui.screen.login.LoginState
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.NameFormat
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel(
    private val connectivityObserver: NetworkConnectivityObserver,
    private val companiesRepository: CompaniesRepository,
    private val restaurantsRepository: RestaurantsRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _isLogged = MutableLiveData(false)
    val isLogged: LiveData<Boolean> = _isLogged

    private val _message = MutableSharedFlow<Message>()
    val message = _message.asSharedFlow()

    private var _networkStatus = ConnectivityObserver.Status.Available

    private var companyId = 0
    private var restaurantId = 0

    private var networkObserverJob: Job? = null
    private var getCompaniesJob: Job? = null
    private var getRestaurantsJob: Job? = null
    private var reloadCompaniesJob: Job? = null
    private var reloadRestaurantsJob: Job? = null
    private var selectCompanyJob: Job? = null
    private var selectRestaurantJob: Job? = null
    private var loginJob: Job? = null
    private var defaultJob: Job? = null

    init {
        observeNetworkChange()
        reloadCompanies()
        getCompanies()
        getRestaurants()
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.SearchCompany -> getCompanies(event.query)
            is LoginEvent.SearchRestaurant -> getRestaurants(event.query)
            is LoginEvent.SelectCompany -> {
                selectCompany(event.id)
                getRestaurants()
            }
            is LoginEvent.SelectRestaurant -> selectRestaurant(event.id)
            LoginEvent.ReloadCompanies -> reloadCompanies()
            LoginEvent.ReloadRestaurants -> {
                if (companyId == 0) {
                    defaultJob?.cancel()
                    defaultJob = viewModelScope.launch {
                        _message.emit(Message.StringResource(R.string.company_required))
                    }
                    return
                }
                reloadRestaurants()
            }
            is LoginEvent.Login -> login(event.credentials)
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

    fun getCompanies(query: String = "") {
        getCompaniesJob?.cancel()
        getCompaniesJob = viewModelScope.launch(Dispatchers.IO) {
            companiesRepository.getAllCompanies(query).collectLatest { result ->
                when (result) {
                    is Resource.Error -> _message.emit(result.message)
                    is Resource.Success -> _state.update {
                        it.copy(
                            companies = result.data.map { it1 ->
                                it1.copy(
                                    name = NameFormat.format(
                                        it1.name
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    fun getRestaurants(query: String = "") {
        getRestaurantsJob?.cancel()
        getRestaurantsJob = viewModelScope.launch(Dispatchers.IO) {
            restaurantsRepository.getAllRestaurants(query, companyId).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        _state.update {
                            it.copy(restaurants = listOf())
                        }
                        _message.emit(result.message)
                    }
                    is Resource.Success -> _state.update {
                        it.copy(
                            restaurants = result.data.map { it1 ->
                                it1.copy(
                                    name = NameFormat.format(
                                        it1.name
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    fun reloadCompanies() {
        reloadCompaniesJob?.cancel()
        reloadCompaniesJob = viewModelScope.launch(Dispatchers.IO) {
            if (!isNetworkAvailable()) return@launch
            val result = companiesRepository.fetchCompanies()
            if (result is Resource.Error) _message.emit(result.message)
        }
    }

    fun reloadRestaurants() {
        reloadRestaurantsJob?.cancel()
        reloadRestaurantsJob = viewModelScope.launch(Dispatchers.IO) {
            if (!isNetworkAvailable()) return@launch
            val result = restaurantsRepository.fetchRestaurants(companyId)
            if (result is Resource.Error) _message.emit(result.message)
        }
    }

    fun selectCompany(id: Int) {
        selectCompanyJob?.cancel()
        selectCompanyJob = viewModelScope.launch(Dispatchers.IO) {
            companyId = id
            val result = companiesRepository.getCompany(companyId)
            if (result is Resource.Success)
                _state.update {
                    _state.value.copy(
                        selectedCompany = NameFormat.format(result.data.name)
                    )
                }
        }
    }

    fun selectRestaurant(id: Int) {
        selectRestaurantJob?.cancel()
        selectRestaurantJob = viewModelScope.launch(Dispatchers.IO) {
            restaurantId = id
            val result = restaurantsRepository.getRestaurant(restaurantId, companyId)
            if (result is Resource.Success)
                _state.update {
                    _state.value.copy(
                        selectedRestaurant = NameFormat.format(result.data.name)
                    )
                }
        }
    }

    fun login(credentials: Credentials) {
        loginJob?.cancel()
        loginJob = viewModelScope.launch(Dispatchers.IO) {

            credentials.validate().let { result ->
                if (result is Resource.Error) {
                    _message.emit(result.message)
                    return@launch
                }
            }

            if (companyId == 0) {
                _message.emit(Message.StringResource(R.string.company_required))
                return@launch
            }
            if (restaurantId == 0) {
                _message.emit(Message.StringResource(R.string.restaurant_required))
                return@launch
            }

            if (!isNetworkAvailable()) return@launch
            _state.update {
                it.copy(isLoading = true)
            }

            when (val result =
                sessionRepository.fetchSession(
                    credentials,
                    companyId,
                    restaurantId
                )) {
                is Resource.Error -> {
                    _state.update {
                        it.copy(isLoading = false)
                    }
                    _message.emit(result.message)
                }
                is Resource.Success -> {
                    _isLogged.postValue(true)
                }
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