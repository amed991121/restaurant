package com.savent.restaurant.data.remote.service

import com.savent.restaurant.data.common.model.Payment
import retrofit2.Response
import retrofit2.http.*

interface PaymentsApiService {

    @GET
    suspend fun getPayments(
        @Query("restaurantId") restaurantId: Int
    ): Response<List<Payment>>

    @POST
    suspend fun insertPayment(
        @Query("restaurantId") restaurantId: Int,
        @Body payment: Payment
    ): Response<Int>

    @PUT
    suspend fun updatePayment(
        @Query("restaurantId") restaurantId: Int,
        @Body payment: Payment
    ): Response<Int>

    @DELETE
    suspend fun deletePayment(
        @Query("restaurantId") restaurantId: Int,
        @Query("paymentId") paymentId: Int
    ): Response<Int>
}