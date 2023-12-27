package com.savent.restaurant.data.local.datasource

import com.savent.restaurant.data.common.model.Session
import com.savent.restaurant.utils.Resource

interface SessionLocalDatasource {
    suspend fun saveSession(session: Session): Resource<Int>
    suspend fun getSession(): Resource<Session>
}