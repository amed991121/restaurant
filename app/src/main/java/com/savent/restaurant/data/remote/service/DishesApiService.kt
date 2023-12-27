package com.savent.restaurant.data.remote.service

import com.savent.restaurant.AppConstants
import com.savent.restaurant.data.common.model.Dish
import retrofit2.Response
import retrofit2.http.*

interface DishesApiService {

    @GET(AppConstants.DISHES_API_PATH)
    suspend fun getDishes(
        @Query("restaurantId") restaurantId: Int,
        @Query("companyId") companyId: Int
    ): Response<List<Dish>>

    @POST
    suspend fun insertDish(
        @Query("restaurantId") restaurantId: Int,
        @Body dish: Dish
    ): Response<Int>

    @PUT
    suspend fun updateDish(
        @Query("restaurantId") restaurantId: Int,
        @Body diner: Dish
    ): Response<Int>

    @DELETE
    suspend fun deleteDish(
        @Query("restaurantId") restaurantId: Int,
        @Query("dishId") dishId: Int
    ): Response<Int>
}