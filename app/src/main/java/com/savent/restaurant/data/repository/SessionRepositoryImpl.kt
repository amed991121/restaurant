package com.savent.restaurant.data.repository

import com.savent.restaurant.data.common.model.Session
import com.savent.restaurant.data.local.datasource.SessionLocalDatasource
import com.savent.restaurant.data.remote.datasource.SessionRemoteDatasource
import com.savent.restaurant.ui.screen.login.Credentials
import com.savent.restaurant.utils.Resource

class SessionRepositoryImpl(
    private val localDatasource: SessionLocalDatasource,
    private val remoteDatasource: SessionRemoteDatasource
) : SessionRepository {
    override suspend fun getSession(): Resource<Session> =
        localDatasource.getSession()

    override suspend fun fetchSession(
        credentials: Credentials,
        companyId: Int,
        restaurantId: Int
    ): Resource<Int> {
        return when (val response =
            remoteDatasource.getSession(credentials, companyId, restaurantId)) {
            is Resource.Success -> localDatasource.saveSession(response.data)
            is Resource.Error -> Resource.Error(response.message)
        }
    }
}