package com.savent.restaurant.data.remote.datasource

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Session
import com.savent.restaurant.data.remote.ErrorBody
import com.savent.restaurant.data.remote.service.SessionApiService
import com.savent.restaurant.ui.screen.login.Credentials
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SessionRemoteDatasourceImpl(private val sessionApiService: SessionApiService) :
    SessionRemoteDatasource {

    override suspend fun getSession(
        credentials: Credentials,
        companyId: Int,
        restaurantId: Int
    ): Resource<Session> =
        withContext(Dispatchers.IO) {
            try {
                val response = sessionApiService.getSession(credentials, companyId, restaurantId)
                if (response.isSuccessful)
                    return@withContext Resource.Success(response.body()!!)
                Resource.Error(
                    Message.DynamicString(
                        Gson().fromJson(
                            response.errorBody()?.charStream(),
                            ErrorBody::class.java
                        ).message
                    )
                )
            } catch (e: Exception) {
                Resource.Error(Message.StringResource(R.string.login_error))
            }
        }
}