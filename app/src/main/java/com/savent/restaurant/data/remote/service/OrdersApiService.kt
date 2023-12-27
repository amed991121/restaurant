package com.savent.restaurant.data.remote.service

import com.savent.restaurant.AppConstants
import com.savent.restaurant.data.remote.model.Order
import retrofit2.Response
import retrofit2.http.*

interface OrdersApiService {

    @GET(AppConstants.SALES_API_PATH)
    suspend fun getOrders(
        @Query("restaurantId") restaurantId: Int,
        @Query("companyId") companyId: Int
    ): Response<List<Order>>

    @POST(AppConstants.SALES_API_PATH)
    suspend fun insertOrder(
        @Query("restaurantId") restaurantId: Int,
        @Query("companyId") companyId: Int,
        @Query("employeeId") employeeId: Int,
        @Query("branchId") branchId: Int,
        @Body order: Order
    ): Response<Int>

    @PUT
    suspend fun updateOrder(
        @Query("restaurantId") restaurantId: Int,
        @Body order: Order
    ): Response<Int>

    @DELETE
    suspend fun deleteOrder(
        @Query("restaurantId") restaurantId: Int,
        @Query("dinerId") orderId: Int
    ): Response<Int>
}