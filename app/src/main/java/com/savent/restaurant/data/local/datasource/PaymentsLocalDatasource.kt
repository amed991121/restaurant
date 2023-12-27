package com.savent.restaurant.data.local.datasource

import com.savent.restaurant.data.common.model.Payment
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

interface PaymentsLocalDatasource {

    suspend fun addPayment(payment: Payment): Resource<Int>

    suspend fun getPayment(id: Int): Resource<Payment>

    fun getPayments(): Flow<Resource<List<Payment>>>

    suspend fun upsertPayments(payments: List<Payment>): Resource<Int>
}