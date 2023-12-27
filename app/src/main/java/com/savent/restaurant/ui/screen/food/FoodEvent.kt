package com.savent.restaurant.ui.screen.food

import com.savent.restaurant.data.common.model.Dish

sealed class FoodEvent {
    class SearchDishes(val query: String, val category: Dish.Category): FoodEvent()
    class SelectDish(val id: Int): FoodEvent()
    class SearchTablesAndOrders(val query: String): FoodEvent()
    class DishToCurrentOrder(val orderId: Int, val isAdded: Boolean): FoodEvent()
    class DishToTable(val tableId: Int, val isAdded: Boolean): FoodEvent()
    object SaveDishOrder: FoodEvent()
}