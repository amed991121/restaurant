package com.savent.restaurant

import com.savent.restaurant.data.*
import com.savent.restaurant.data.dependency.dishesDataModule
import com.savent.restaurant.data.dependency.kitchenNotesDataModule
import com.savent.restaurant.data.dependency.printersDataModule
import com.savent.restaurant.data.dependency.sessionDataModule
import com.savent.restaurant.domain.usecase.*
import com.savent.restaurant.ui.viewmodel.*
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val splashModule = module {
    includes(sessionDataModule)
    viewModel { SplashViewModel(get()) }
}

val loginModule = module {
    includes(sessionDataModule, companiesDataModule, restaurantsDataModule)
    viewModel { LoginViewModel(get(), get(), get(), get()) }
}

val foodMenuModule = module {
    includes(sessionDataModule, dishesDataModule, tablesDataModule, ordersDataModule)

    single {
        GetTablesAndOrdersUseCase(get(), get())
    }

    single {
        CreateOrderUseCase(get(), get())
    }

    single {
        ComputeOrderUseCase(get(), get())
    }

    single {
        AddDishToOrderUseCase(get(), get(), get())
    }

    single {
        GetMenuCategoriesWithImageUseCase()
    }

    single {
        GetMenuCategoriesWithIconUseCase()
    }

    viewModel { FoodViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
}

val ordersModule = module {
    includes(
        sessionDataModule,
        tablesDataModule,
        dishesDataModule,
        dinersDataModule,
        ordersDataModule,
        printersDataModule,
        kitchenNotesDataModule
    )

    single {
        CreateOrderUseCase(get(), get())
    }

    single {
        GetOpenOrdersUseCase(get(), get())
    }

    single {
        GetMenuCategoriesWithIconUseCase()
    }

    single {
        TableHasOrderUseCase(get())
    }

    single {
        ComputeOrderUseCase(get(), get())
    }

    single {
        AddDishUnitUseCase(get(), get())
    }

    single {
        RemoveDishUnitUseCase(get(), get())
    }

    single {
        GePendingDishesSendToKitchenUseCase(get(),get(),get())
    }

    single {
        GetOpenOrderUseCase(get(), get(), get(), get())
    }

    single {
        PaymentMethodUseCase()
    }

    single {
        GetKitchenNoteUseCase(get(),get(),get(),get(),get())
    }

    single { IsGrantedAndroid12BLEPermissionUseCase(androidContext()) }

    single {
        OrdersViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
}

val salesModule = module {
    includes(
        sessionDataModule,
        tablesDataModule,
        ordersDataModule,
        printersDataModule
    )

    single { GetSalesUseCase(get(), get()) }

    single { IsGrantedAndroid12BLEPermissionUseCase(androidContext()) }

    singleOf(::GetReceiptToPrintUseCase)

    singleOf(::GetReceiptToSendUseCase)

    singleOf(::ShareReceiptUseCase)

    viewModel { SalesViewModel(get(), get(), get(), get(), get(), get(), get()) }
}

val appModule = module {
    includes(splashModule, loginModule, foodMenuModule, ordersModule, salesModule)
}
