package com.savent.restaurant.data.repository

import com.savent.restaurant.data.common.model.Diner
import com.savent.restaurant.data.local.datasource.DinersLocalDatasource
import com.savent.restaurant.data.remote.datasource.DinersRemoteDatasource
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

class DinersRepositoryImpl(
    private val localDatasource: DinersLocalDatasource,
    private val remoteDatasource: DinersRemoteDatasource,
) : DinersRepository {

    override suspend fun upsertDiners(diners: List<Diner>): Resource<Int> =
        localDatasource.upsertDiners(diners)

    override suspend fun addDiner(restaurantId: Int, diner: Diner): Resource<Int> {
        val response = remoteDatasource.insertDiner(restaurantId, diner)
        if (response is Resource.Error)
            return response
        return localDatasource.addDiner(diner)
    }

    override suspend fun getDiner(id: Int): Resource<Diner> =
        localDatasource.getDiner(id)

    override fun getAllDiners(query: String): Flow<Resource<List<Diner>>> =
        localDatasource.getDiners(query)

    override suspend fun fetchDiners(restaurantId: Int, companyId: Int): Resource<Int> {
        return when (val response = remoteDatasource.getDiners(restaurantId,companyId)) {
            is Resource.Success -> localDatasource.upsertDiners(response.data)
            is Resource.Error -> Resource.Error(response.message)
        }
    }

    override suspend fun updateDiner(restaurantId: Int, diner: Diner): Resource<Int> =
        remoteDatasource.updateDiner(restaurantId, diner)

    override suspend fun deleteDiner(restaurantId: Int, id: Int): Resource<Int> =
        remoteDatasource.deleteDiner(restaurantId, id)

}