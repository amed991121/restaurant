package com.savent.restaurant.data.remote.datasource

import com.savent.restaurant.data.common.model.Restaurant
import com.savent.restaurant.utils.Resource

interface RestaurantsRemoteDatasource {
    suspend fun getRestaurants(companyId: Int): Resource<List<Restaurant>>
}