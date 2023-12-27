package com.savent.restaurant.data.repository

import com.savent.restaurant.data.common.model.Debt
import com.savent.restaurant.data.local.datasource.DebtsLocalDatasource
import com.savent.restaurant.data.remote.datasource.DebtsRemoteDatasource
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

class DebtsRepositoryImpl(
    private val localDatasource: DebtsLocalDatasource,
    private val remoteDatasource: DebtsRemoteDatasource,
) : DebtsRepository {

    override suspend fun upsertDebts(debts: List<Debt>): Resource<Int> =
        localDatasource.upsertDebts(debts)

    override suspend fun addDebt(restaurantId: Int, debt: Debt): Resource<Int> =
        remoteDatasource.insertDebt(restaurantId, debt)


    override suspend fun getDebt(id: Int): Resource<Debt> =
        localDatasource.getDebt(id)

    override fun getAllDebts(): Flow<Resource<List<Debt>>> =
        localDatasource.getDebts()

    override suspend fun fetchDebts(restaurantId: Int): Resource<Int> {
        return when (val response = remoteDatasource.getDebts(restaurantId)) {
            is Resource.Success -> localDatasource.upsertDebts(response.data)
            is Resource.Error -> Resource.Error(response.message)
        }
    }

    override suspend fun updateDebt(restaurantId: Int, debt: Debt): Resource<Int> =
        remoteDatasource.updateDebt(restaurantId, debt)

    override suspend fun deleteDebt(restaurantId: Int, id: Int): Resource<Int> =
        remoteDatasource.deleteDebt(restaurantId, id)
}