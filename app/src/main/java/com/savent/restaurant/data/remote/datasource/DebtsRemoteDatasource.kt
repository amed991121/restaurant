package com.savent.restaurant.data.remote.datasource

import com.savent.restaurant.data.common.model.Debt
import com.savent.restaurant.utils.Resource

interface DebtsRemoteDatasource {

    suspend fun getDebts(restaurantId: Int): Resource<List<Debt>>

    suspend fun insertDebt(restaurantId: Int, debt: Debt): Resource<Int>

    suspend fun updateDebt(restaurantId: Int, debt: Debt): Resource<Int>

    suspend fun deleteDebt(restaurantId: Int, debtId: Int): Resource<Int>
}