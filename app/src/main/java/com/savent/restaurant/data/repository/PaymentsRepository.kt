package com.savent.restaurant.data.repository

import com.savent.restaurant.data.common.model.Payment
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

interface PaymentsRepository {

    suspend fun upsertPayments(payments: List<Payment>): Resource<Int>

    suspend fun addPayment(restaurantId: Int, payment: Payment): Resource<Int>

    suspend fun getPayment(id: Int): Resource<Payment>

    fun getAllPayments(): Flow<Resource<List<Payment>>>

    suspend fun fetchPayments(restaurantId: Int): Resource<Int>

    suspend fun updatePayment(restaurantId: Int, payment: Payment): Resource<Int>

    suspend fun deletePayment(restaurantId: Int, id: Int): Resource<Int>
}