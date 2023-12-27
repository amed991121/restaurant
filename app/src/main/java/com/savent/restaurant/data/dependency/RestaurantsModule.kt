package com.savent.restaurant.data

import com.savent.restaurant.data.dependency.baseModule
import com.savent.restaurant.data.local.database.AppDatabase
import com.savent.restaurant.data.local.datasource.RestaurantsLocalDatasource
import com.savent.restaurant.data.local.datasource.RestaurantsLocalDatasourceImpl
import com.savent.restaurant.data.remote.datasource.RestaurantsRemoteDatasource
import com.savent.restaurant.data.remote.datasource.RestaurantsRemoteDatasourceImpl
import com.savent.restaurant.data.remote.service.RestaurantsApiService
import com.savent.restaurant.data.repository.RestaurantsRepository
import com.savent.restaurant.data.repository.RestaurantsRepositoryImpl
import org.koin.dsl.module
import retrofit2.Retrofit

val restaurantsDataModule = module {
    includes(baseModule)

    single {
        get<AppDatabase>().restaurantDao()
    }

    single<RestaurantsLocalDatasource> {
        RestaurantsLocalDatasourceImpl(get())
    }

    single<RestaurantsApiService> {
        get<Retrofit>().create(RestaurantsApiService::class.java)
    }

    single<RestaurantsRemoteDatasource> {
        RestaurantsRemoteDatasourceImpl(get())
    }

    single<RestaurantsRepository> {
        RestaurantsRepositoryImpl(get(), get())
    }
}