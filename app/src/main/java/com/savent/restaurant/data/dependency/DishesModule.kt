package com.savent.restaurant.data.dependency

import com.savent.restaurant.data.local.database.AppDatabase
import com.savent.restaurant.data.local.datasource.DishesLocalDatasource
import com.savent.restaurant.data.local.datasource.DishesLocalDatasourceImpl
import com.savent.restaurant.data.remote.datasource.DishesRemoteDatasource
import com.savent.restaurant.data.remote.datasource.DishesRemoteDatasourceImpl
import com.savent.restaurant.data.remote.service.DishesApiService
import com.savent.restaurant.data.repository.DishesRepository
import com.savent.restaurant.data.repository.DishesRepositoryImpl
import org.koin.dsl.module
import retrofit2.Retrofit

val dishesDataModule = module {
    includes(baseModule)

    single {
        get<AppDatabase>().dishDao()
    }

    single<DishesLocalDatasource> {
        DishesLocalDatasourceImpl(get())
    }

    single<DishesApiService> {
        get<Retrofit>().create(DishesApiService::class.java)
    }

    single<DishesRemoteDatasource> {
        DishesRemoteDatasourceImpl(get())
    }

    single<DishesRepository> {
        DishesRepositoryImpl(get(), get())
    }
}