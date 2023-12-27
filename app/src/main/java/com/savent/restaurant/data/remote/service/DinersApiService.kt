package com.savent.restaurant.data.remote.service

import com.savent.restaurant.AppConstants
import com.savent.restaurant.data.common.model.Diner
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface DinersApiService {

    @GET(AppConstants.DINERS_API_PATH)
    suspend fun getDiners(
        @Query("restaurantId") restaurantId: Int,
        @Query("companyId") companyId: Int
    ): Response<List<Diner>>

    @POST
    suspend fun insertDiner(
        @Query("restaurantId") restaurantId: Int,
        @Body diner: Diner
    ): Response<Int>

    @PUT
    suspend fun updateDiner(
        @Query("restaurantId") restaurantId: Int,
        @Body diner: Diner
    ): Response<Int>

    @DELETE
    suspend fun deleteDiner(
        @Query("restaurantId") restaurantId: Int,
        @Query("dinerId") dinerId: Int
    ): Response<Int>
}