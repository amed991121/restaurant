package com.savent.restaurant.data

import com.savent.restaurant.data.dependency.baseModule
import com.savent.restaurant.data.local.database.AppDatabase
import com.savent.restaurant.data.local.datasource.DinersLocalDatasource
import com.savent.restaurant.data.local.datasource.DinersLocalDatasourceImpl
import com.savent.restaurant.data.remote.datasource.DinersRemoteDatasource
import com.savent.restaurant.data.remote.datasource.DinersRemoteDatasourceImpl
import com.savent.restaurant.data.remote.service.DinersApiService
import com.savent.restaurant.data.repository.DinersRepository
import com.savent.restaurant.data.repository.DinersRepositoryImpl
import org.koin.dsl.module
import retrofit2.Retrofit

val dinersDataModule = module {
    includes(baseModule)

    single {
        get<AppDatabase>().dinerDao()
    }

    single<DinersLocalDatasource> {
        DinersLocalDatasourceImpl(get())
    }

    single<DinersApiService> {
        get<Retrofit>().create(DinersApiService::class.java)
    }

    single<DinersRemoteDatasource> {
        DinersRemoteDatasourceImpl(get())
    }

    single<DinersRepository> {
        DinersRepositoryImpl(get(), get())
    }
}