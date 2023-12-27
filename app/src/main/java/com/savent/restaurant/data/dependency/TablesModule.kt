package com.savent.restaurant.data

import com.savent.restaurant.data.dependency.baseModule
import com.savent.restaurant.data.local.database.AppDatabase
import com.savent.restaurant.data.local.datasource.TablesLocalDatasource
import com.savent.restaurant.data.local.datasource.TablesLocalDatasourceImpl
import com.savent.restaurant.data.remote.datasource.TablesRemoteDatasource
import com.savent.restaurant.data.remote.datasource.TablesRemoteDatasourceImpl
import com.savent.restaurant.data.remote.service.TablesApiService
import com.savent.restaurant.data.repository.TablesRepository
import com.savent.restaurant.data.repository.TablesRepositoryImpl
import org.koin.dsl.module
import retrofit2.Retrofit

val tablesDataModule = module {
    includes(baseModule)

    single {
        get<AppDatabase>().tableDao()
    }

    single<TablesLocalDatasource> {
        TablesLocalDatasourceImpl(get())
    }

    single<TablesApiService> {
        get<Retrofit>().create(TablesApiService::class.java)
    }

    single<TablesRemoteDatasource> {
        TablesRemoteDatasourceImpl(get())
    }

    single<TablesRepository> {
        TablesRepositoryImpl(get(), get())
    }
}