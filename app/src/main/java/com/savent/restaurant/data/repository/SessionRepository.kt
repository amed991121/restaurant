package com.savent.restaurant.data.repository

import com.savent.restaurant.data.common.model.Session
import com.savent.restaurant.ui.screen.login.Credentials
import com.savent.restaurant.utils.Resource

interface SessionRepository {

    suspend fun getSession(): Resource<Session>

    suspend fun fetchSession(
        credentials: Credentials,
        companyId: Int,
        restaurantId: Int
    ): Resource<Int>
}