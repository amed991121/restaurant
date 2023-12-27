package com.savent.restaurant.data.local

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.google.gson.Gson
import com.savent.restaurant.R
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.*
import java.lang.reflect.Type

class DataObjectStorage<T> constructor(
    private val gson: Gson,
    private val type: Type,
    private val dataStore: DataStore<Preferences>,
    private val preferenceKey: Preferences.Key<String>
) {
    suspend fun saveData(data: T): Resource<Int> {
        try {
            dataStore.edit {
                val jsonString = gson.toJson(data, type)
                it[preferenceKey] = jsonString
            }
        } catch (e: Exception) {
            return Resource.Error(Message.StringResource(R.string.save_data_error))
        }
        return Resource.Success(0)
    }

    fun getData(): Flow<Resource<T>> = flow {
        dataStore.data.map { preferences ->
            val jsonString = preferences[preferenceKey]
            val elements = gson.fromJson<T>(jsonString, type)
            elements
        }.catch {
            emit(Resource.Error(Message.StringResource(R.string.retrieve_data_error)))
        }.collect {
            if (it == null)
                emit(Resource.Error(Message.StringResource(R.string.retrieve_data_error)))
            else
                emit(Resource.Success(it))
        }

    }

}