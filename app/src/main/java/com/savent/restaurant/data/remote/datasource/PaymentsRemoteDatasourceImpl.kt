package com.savent.restaurant.data.remote.datasource

import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Payment
import com.savent.restaurant.data.remote.service.PaymentsApiService
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource

class PaymentsRemoteDatasourceImpl(private val paymentsApiService: PaymentsApiService) :
    PaymentsRemoteDatasource {

    override suspend fun getPayments(restaurantId: Int): Resource<List<Payment>> {
        try {
            val response = paymentsApiService.getPayments(restaurantId)
            if (response.isSuccessful)
                return Resource.Success(response.body()!!)
            return Resource.Error(Message.DynamicString(response.errorBody().toString()))
        } catch (e: Exception) {
            return Resource.Error(Message.StringResource(R.string.get_payments_error))
        }
    }

    override suspend fun insertPayment(restaurantId: Int, payment: Payment): Resource<Int> {
        try {
            val response = paymentsApiService.insertPayment(restaurantId, payment)
            if (response.isSuccessful)
                return Resource.Success(response.body()!!)
            return Resource.Error(Message.DynamicString(response.errorBody().toString()))
        }catch (e: Exception) {
            return Resource.Error(Message.StringResource(R.string.add_payment_error))
        }
    }

    override suspend fun updatePayment(restaurantId: Int, payment: Payment): Resource<Int> {
        try {
            val response = paymentsApiService.updatePayment(restaurantId, payment)
            if (response.isSuccessful)
                return Resource.Success(response.body()!!)
            return Resource.Error(Message.DynamicString(response.errorBody().toString()))
        }catch (e: Exception) {
            return Resource.Error(Message.StringResource(R.string.update_payment_error))
        }
    }

    override suspend fun deletePayment(restaurantId: Int, paymentId: Int): Resource<Int> {
        try {
            val response = paymentsApiService.deletePayment(restaurantId, paymentId)
            if (response.isSuccessful)
                return Resource.Success(response.body()!!)
            return Resource.Error(Message.DynamicString(response.errorBody().toString()))
        }catch (e: Exception) {
            return Resource.Error(Message.StringResource(R.string.delete_payment_error))
        }
    }
}