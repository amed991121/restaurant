package com.savent.restaurant.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.savent.restaurant.data.common.model.Payment
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PaymentDao : BaseDao<Payment>() {

    override fun getTableName(): String = "payments"

    @Query("SELECT * FROM payments")
    abstract fun getAllPayments(): Flow<List<Payment>>
}