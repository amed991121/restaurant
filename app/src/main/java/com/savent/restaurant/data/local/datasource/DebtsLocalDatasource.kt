package com.savent.restaurant.data.local.datasource

import com.savent.restaurant.data.common.model.Debt
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

interface DebtsLocalDatasource {

    suspend fun getDebt(id: Int): Resource<Debt>

    fun getDebts(): Flow<Resource<List<Debt>>>

    suspend fun upsertDebts(debts: List<Debt>): Resource<Int>
}