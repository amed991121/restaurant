package com.savent.restaurant.data.remote.datasource

import com.savent.restaurant.data.common.model.Session
import com.savent.restaurant.ui.screen.login.Credentials
import com.savent.restaurant.utils.Resource

interface SessionRemoteDatasource {
    suspend fun getSession(
        credentials: Credentials,
        companyId: Int,
        restaurantId: Int
    ): Resource<Session>
}