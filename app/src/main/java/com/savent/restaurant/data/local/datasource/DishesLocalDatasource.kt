package com.savent.restaurant.data.local.datasource

import com.savent.restaurant.data.common.model.Dish
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

interface DishesLocalDatasource {

    suspend fun addDish(dish: Dish): Resource<Int>

    suspend fun getDish(id: Int): Resource<Dish>

    fun getDishes(query: String, category: Dish.Category): Flow<Resource<List<Dish>>>

    suspend fun getDishes(listId: List<Int>): Resource<List<Dish>>

    suspend fun upsertDishes(dishes: List<Dish>): Resource<Int>
}