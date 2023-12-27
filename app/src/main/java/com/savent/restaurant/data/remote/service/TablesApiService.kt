package com.savent.restaurant.data.remote.service

import com.savent.restaurant.AppConstants
import com.savent.restaurant.data.common.model.Table
import retrofit2.Response
import retrofit2.http.*

interface TablesApiService {

    @GET(AppConstants.TABLES_API_PATH)
    suspend fun getTables(
        @Query("restaurantId") restaurantId: Int,
        @Query("companyId") companyId: Int
    ): Response<List<Table>>

    @POST
    suspend fun insertTable(
        @Query("restaurantId") restaurantId: Int,
        @Body table: Table
    ): Response<Int>

    @PUT
    suspend fun updateTable(
        @Query("restaurantId") restaurantId: Int,
        @Body table: Table
    ): Response<Int>

    @DELETE
    suspend fun deleteTable(
        @Query("restaurantId") restaurantId: Int,
        @Query("tableId") tableId: Int
    ): Response<Int>
}