package com.savent.restaurant.data.dependency

import com.savent.restaurant.data.local.database.AppDatabase
import com.savent.restaurant.data.repository.KitchenNotesRepository
import com.savent.restaurant.data.repository.KitchenNotesRepositoryImpl
import org.koin.dsl.module

val kitchenNotesDataModule = module {
    includes(baseModule)

    single {
        get<AppDatabase>().kitchenNoteDao()
    }

    single<KitchenNotesRepository> {
        KitchenNotesRepositoryImpl(get())
    }
}