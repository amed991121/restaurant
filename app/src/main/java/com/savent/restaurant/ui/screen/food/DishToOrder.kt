package com.savent.restaurant.ui.screen.food

data class DishToOrder (
    val dishId: Int = 0,
    val tablesIdList: List<Int> = listOf(),
    val ordersIdList: List<Int> = listOf(),
)