package com.savent.restaurant.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.savent.restaurant.data.common.model.Debt
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DebtDao : BaseDao<Debt>() {

    override fun getTableName(): String = "debts"

    @Query("SELECT * FROM debts")
    abstract fun getAllDebts(): Flow<List<Debt>>
}