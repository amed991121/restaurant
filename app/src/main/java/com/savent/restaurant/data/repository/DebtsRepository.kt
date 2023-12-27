package com.savent.restaurant.data.repository

import com.savent.restaurant.data.common.model.Debt
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

interface DebtsRepository {

    suspend fun upsertDebts(debts: List<Debt>): Resource<Int>

    suspend fun addDebt(restaurantId: Int, debt: Debt): Resource<Int>

    suspend fun getDebt(id: Int): Resource<Debt>

    fun getAllDebts(): Flow<Resource<List<Debt>>>

    suspend fun fetchDebts(restaurantId: Int): Resource<Int>

    suspend fun updateDebt(restaurantId: Int, debt: Debt): Resource<Int>

    suspend fun deleteDebt(restaurantId: Int, id: Int): Resource<Int>
}