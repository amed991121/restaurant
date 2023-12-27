package com.savent.restaurant.ui.navigation

sealed class Screen(val route: String) {
    object FoodMenu : Screen("food_menu")
    object Orders : Screen("orders"){
        object List : Screen("${route}/list")
        object Order : Screen("${route}/new")
        object Register : Screen("${route}/register")
    }
    object Sales : Screen("sales")

}