package com.savent.restaurant.data.local.datasource

import com.savent.restaurant.data.common.model.Restaurant
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

interface RestaurantsLocalDatasource {

    suspend fun getRestaurant(id: Int, companyId: Int): Resource<Restaurant>

    fun getRestaurants(query: String, companyId: Int): Flow<Resource<List<Restaurant>>>

    suspend fun upsertRestaurants(restaurants: List<Restaurant>): Resource<Int>

}