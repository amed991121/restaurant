package com.savent.restaurant.data.repository

import com.savent.restaurant.data.common.model.Dish
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

interface DishesRepository {

    suspend fun upsertDishes(dishes: List<Dish>): Resource<Int>

    suspend fun addDish(restaurantId: Int, dish: Dish): Resource<Int>

    suspend fun getDish(id: Int): Resource<Dish>

    fun getAllDishes(query: String, category: Dish.Category): Flow<Resource<List<Dish>>>

    suspend fun getDishes(listId: List<Int>): Resource<List<Dish>>

    suspend fun fetchDishes(restaurantId: Int, companyId: Int): Resource<Int>

    suspend fun updateDish(restaurantId: Int, dish: Dish): Resource<Int>

    suspend fun deleteDish(restaurantId: Int, id: Int): Resource<Int>
}