package com.savent.restaurant.data.remote.service

import com.savent.restaurant.AppConstants
import com.savent.restaurant.data.common.model.Restaurant
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RestaurantsApiService {

    @GET(AppConstants.RESTAURANTS_API_PATH)
    suspend fun getRestaurants(
        @Query("companyId") companyId: Int
    ): Response<List<Restaurant>>
}