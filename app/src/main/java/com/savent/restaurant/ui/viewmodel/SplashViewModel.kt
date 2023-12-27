package com.savent.restaurant.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savent.restaurant.data.repository.SessionRepository
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(private val sessionRepository: SessionRepository): ViewModel() {
    private val _isLogged = MutableLiveData<Boolean?>(null)
    val isLogged: LiveData<Boolean?> = _isLogged
    var job: Job? = null
    fun checkLogin(){
        job?.cancel()
        job = viewModelScope.launch{
            when(sessionRepository.getSession()){
                is Resource.Error -> _isLogged.postValue(false)
                is Resource.Success -> _isLogged.postValue(true)
            }
        }
    }

}