package com.savent.restaurant.data

import com.savent.restaurant.data.dependency.baseModule
import com.savent.restaurant.data.local.database.AppDatabase
import com.savent.restaurant.data.local.datasource.CompaniesLocalDatasource
import com.savent.restaurant.data.local.datasource.CompaniesLocalDatasourceImpl
import com.savent.restaurant.data.remote.datasource.CompaniesRemoteDatasource
import com.savent.restaurant.data.remote.datasource.CompaniesRemoteDatasourceImpl
import com.savent.restaurant.data.remote.service.CompaniesApiService
import com.savent.restaurant.data.repository.CompaniesRepository
import com.savent.restaurant.data.repository.CompaniesRepositoryImpl
import org.koin.dsl.module
import retrofit2.Retrofit

val companiesDataModule = module {
    includes(baseModule)

    single {
        get<AppDatabase>().companyDao()
    }

    single<CompaniesLocalDatasource> {
        CompaniesLocalDatasourceImpl(get())
    }

    single<CompaniesApiService> {
        get<Retrofit>().create(CompaniesApiService::class.java)
    }

    single<CompaniesRemoteDatasource> {
        CompaniesRemoteDatasourceImpl(get())
    }

    single<CompaniesRepository> {
        CompaniesRepositoryImpl(get(), get())
    }
}