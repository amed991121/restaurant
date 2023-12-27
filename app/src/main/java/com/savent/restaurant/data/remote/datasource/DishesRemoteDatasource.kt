package com.savent.restaurant.data.remote.datasource

import com.savent.restaurant.data.common.model.Dish
import com.savent.restaurant.utils.Resource

interface DishesRemoteDatasource {

    suspend fun getDishes(restaurantId: Int, companyId: Int): Resource<List<Dish>>

    suspend fun insertDish(restaurantId: Int, dish: Dish): Resource<Int>

    suspend fun updateDish(restaurantId: Int, dish: Dish): Resource<Int>

    suspend fun deleteDish(restaurantId: Int, dishId: Int): Resource<Int>
}