package com.savent.restaurant.data.local.datasource

import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Payment
import com.savent.restaurant.data.local.database.dao.PaymentDao
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class PaymentsLocalDatasourceImpl(private val paymentDao: PaymentDao) : PaymentsLocalDatasource {

    override suspend fun addPayment(payment: Payment): Resource<Int> = withContext(Dispatchers.IO) {
        val result = paymentDao.insert(payment)
        if (result > 0L) Resource.Success(result.toInt())
        Resource.Error(
            Message.StringResource(R.string.add_payment_error)
        )
    }

    override suspend fun getPayment(id: Int): Resource<Payment> = withContext(Dispatchers.IO) {
        val result = paymentDao.get(id)
        if (result != null) Resource.Success(result)
        Resource.Error(
            Message.StringResource(R.string.payment_not_found)
        )
    }

    override fun getPayments(): Flow<Resource<List<Payment>>> = flow {
        paymentDao.getAllPayments().onEach {
            emit(Resource.Success(it))
        }.catch {
            Resource.Error<List<Payment>>(
                Message.StringResource(R.string.get_payments_error)
            )
        }.collect()
    }

    override suspend fun upsertPayments(payments: List<Payment>): Resource<Int> =
        synchronized(this) {
            runBlocking(Dispatchers.IO) {
                paymentDao.getAll().forEach { current ->
                    if (payments.find { new -> current.id == new.id } == null)
                        paymentDao.delete(current)
                }
                val result = paymentDao.upsertAll(payments)
                if (result.isEmpty()) Resource.Error<Int>(
                    Message.StringResource(R.string.update_payments_error)
                )
                Resource.Success(result.size)
            }
        }
}