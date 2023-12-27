package com.savent.restaurant.data.remote.datasource

import com.savent.restaurant.data.common.model.Payment
import com.savent.restaurant.utils.Resource

interface PaymentsRemoteDatasource {

    suspend fun getPayments(restaurantId: Int): Resource<List<Payment>>

    suspend fun insertPayment(restaurantId: Int, payment: Payment): Resource<Int>

    suspend fun updatePayment(restaurantId: Int, payment: Payment): Resource<Int>

    suspend fun deletePayment(restaurantId: Int, paymentId: Int): Resource<Int>
}