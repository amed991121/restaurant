package com.savent.restaurant.data.local.datasource

import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Session
import com.savent.restaurant.data.local.DataObjectStorage
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class SessionLocalDatasourceImpl(private val sessionStorage: DataObjectStorage<Session>) :
    SessionLocalDatasource {

    override suspend fun saveSession(session: Session): Resource<Int> =
        withContext(Dispatchers.IO) {
            sessionStorage.saveData(session)
        }

    override suspend fun getSession(): Resource<Session> =
        withContext(Dispatchers.IO) {
            try {
                sessionStorage.getData().first()
            }catch (e: Exception){
                Resource.Error(Message.StringResource(R.string.get_session_error))
            }

        }
}