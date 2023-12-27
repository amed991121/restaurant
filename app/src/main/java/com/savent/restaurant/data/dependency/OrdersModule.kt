package com.savent.restaurant.data

import com.savent.restaurant.data.dependency.baseModule
import com.savent.restaurant.data.local.database.AppDatabase
import com.savent.restaurant.data.local.datasource.OrdersLocalDatasource
import com.savent.restaurant.data.local.datasource.OrdersLocalDatasourceImpl
import com.savent.restaurant.data.remote.datasource.OrdersRemoteDatasource
import com.savent.restaurant.data.remote.datasource.OrdersRemoteDatasourceImpl
import com.savent.restaurant.data.remote.service.OrdersApiService
import com.savent.restaurant.data.repository.OrdersRepository
import com.savent.restaurant.data.repository.OrdersRepositoryImpl
import org.koin.dsl.module
import retrofit2.Retrofit

val ordersDataModule = module {
    includes(baseModule)

    single {
        get<AppDatabase>().orderDao()
    }

    single<OrdersLocalDatasource> {
        OrdersLocalDatasourceImpl(get())
    }

    single<OrdersApiService> {
        get<Retrofit>().create(OrdersApiService::class.java)
    }

    single<OrdersRemoteDatasource> {
        OrdersRemoteDatasourceImpl(get())
    }

    single<OrdersRepository> {
        OrdersRepositoryImpl(get(), get())
    }
}