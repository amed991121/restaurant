package com.savent.restaurant.data.dependency

import com.savent.restaurant.data.local.database.AppDatabase
import com.savent.restaurant.data.repository.PrinterRepository
import com.savent.restaurant.data.repository.PrinterRepositoryImpl
import org.koin.dsl.module

val printersDataModule = module {
    includes(baseModule)

    single {
        get<AppDatabase>().printerDao()
    }

    single<PrinterRepository> {
        PrinterRepositoryImpl(get())
    }
}