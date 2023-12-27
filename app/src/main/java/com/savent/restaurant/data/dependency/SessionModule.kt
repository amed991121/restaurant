package com.savent.restaurant.data.dependency

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.reflect.TypeToken
import com.savent.restaurant.AppConstants
import com.savent.restaurant.data.common.model.Session
import com.savent.restaurant.data.local.DataObjectStorage
import com.savent.restaurant.data.local.datasource.SessionLocalDatasource
import com.savent.restaurant.data.local.datasource.SessionLocalDatasourceImpl
import com.savent.restaurant.data.remote.datasource.SessionRemoteDatasource
import com.savent.restaurant.data.remote.datasource.SessionRemoteDatasourceImpl
import com.savent.restaurant.data.remote.service.SessionApiService
import com.savent.restaurant.data.repository.SessionRepository
import com.savent.restaurant.data.repository.SessionRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit

private val Context.datastore: DataStore<Preferences> by preferencesDataStore(AppConstants.APP_PREFERENCES)

val sessionDataModule = module {

    includes(baseModule)

    single<SessionLocalDatasource> {
        SessionLocalDatasourceImpl(
            DataObjectStorage<Session>(
                get(),
                object : TypeToken<Session>() {}.type,
                androidContext().datastore,
                stringPreferencesKey((AppConstants.SESSION_PREFERENCES))
            )
        )
    }

    single<SessionApiService> {
        get<Retrofit>().create(SessionApiService::class.java)
    }

    single<SessionRemoteDatasource> {
        SessionRemoteDatasourceImpl(get())
    }

    single<SessionRepository> {
        SessionRepositoryImpl(get(), get())
    }

}