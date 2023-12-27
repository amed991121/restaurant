package com.savent.restaurant.data.repository

import com.savent.restaurant.data.common.model.Payment
import com.savent.restaurant.data.local.datasource.PaymentsLocalDatasource
import com.savent.restaurant.data.remote.datasource.PaymentsRemoteDatasource
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

class PaymentsRepositoryImpl(
    private val localDatasource: PaymentsLocalDatasource,
    private val remoteDatasource: PaymentsRemoteDatasource,
) : PaymentsRepository {

    override suspend fun upsertPayments(payments: List<Payment>): Resource<Int> =
        localDatasource.upsertPayments(payments)

    override suspend fun addPayment(restaurantId: Int, payment: Payment): Resource<Int> {
        val response = remoteDatasource.insertPayment(restaurantId, payment)
        if (response is Resource.Error)
            return response
        return localDatasource.addPayment(payment)
    }

    override suspend fun getPayment(id: Int): Resource<Payment> =
        localDatasource.getPayment(id)

    override fun getAllPayments(): Flow<Resource<List<Payment>>> =
        localDatasource.getPayments()

    override suspend fun fetchPayments(restaurantId: Int): Resource<Int> {
        return when (val response = remoteDatasource.getPayments(restaurantId)) {
            is Resource.Success -> localDatasource.upsertPayments(response.data)
            is Resource.Error -> Resource.Error(response.message)
        }
    }

    override suspend fun updatePayment(restaurantId: Int, payment: Payment): Resource<Int> =
        remoteDatasource.updatePayment(restaurantId, payment)

    override suspend fun deletePayment(restaurantId: Int, id: Int): Resource<Int> =
        remoteDatasource.deletePayment(restaurantId, id)
}