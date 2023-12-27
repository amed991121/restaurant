package com.savent.restaurant.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.savent.restaurant.R

sealed class BottomNavItem(
    val route: String,
    @StringRes val titleResId: Int,
    @DrawableRes val iconResId: Int
) {
    object FoodMenu : BottomNavItem(
        route = Screen.FoodMenu.route,
        titleResId = R.string.food_menu,
        iconResId = R.drawable.ic_search
    )

    object Orders : BottomNavItem(
        route = Screen.Orders.route,
        titleResId = R.string.orders,
        iconResId = R.drawable.ic_note
    )

    object Sales : BottomNavItem(
        route = Screen.Sales.route,
        titleResId = R.string.sales,
        iconResId = R.drawable.ic_wallet
    )
}