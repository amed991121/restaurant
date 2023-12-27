package com.savent.restaurant.data.remote.service

import com.savent.restaurant.data.common.model.Debt
import retrofit2.Response
import retrofit2.http.*

interface DebtsApiService {

    @GET
    suspend fun getDebts(
        @Query("restaurantId") restaurantId: Int
    ): Response<List<Debt>>

    @POST
    suspend fun insertDebt(
        @Query("restaurantId") restaurantId: Int,
        @Body debt: Debt
    ): Response<Int>

    @PUT
    suspend fun updateDebt(
        @Query("restaurantId") restaurantId: Int,
        @Body debt: Debt
    ): Response<Int>

    @DELETE
    suspend fun deleteDebt(
        @Query("restaurantId") restaurantId: Int,
        @Query("debtId") debtId: Int
    ): Response<Int>
}