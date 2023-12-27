package com.savent.restaurant.data.repository

import com.savent.restaurant.data.common.model.Restaurant
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

interface RestaurantsRepository {

    suspend fun upsertRestaurants(restaurants: List<Restaurant>): Resource<Int>

    suspend fun getRestaurant(id: Int, companyId: Int): Resource<Restaurant>

    fun getAllRestaurants(query: String, companyId: Int): Flow<Resource<List<Restaurant>>>

    suspend fun fetchRestaurants(companyId: Int): Resource<Int>
}