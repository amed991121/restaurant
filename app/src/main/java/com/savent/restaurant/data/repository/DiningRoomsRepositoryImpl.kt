package com.savent.restaurant.data.repository

import com.savent.restaurant.data.common.model.DiningRoom
import com.savent.restaurant.data.local.datasource.DiningRoomsLocalDatasource
import com.savent.restaurant.data.remote.datasource.DiningRoomsRemoteDatasource
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

class DiningRoomsRepositoryImpl(
    private val localDatasource: DiningRoomsLocalDatasource,
    private val remoteDatasource: DiningRoomsRemoteDatasource,
) : DiningRoomsRepository {

    override suspend fun upsertDiningRooms(diningRooms: List<DiningRoom>): Resource<Int> =
        localDatasource.upsertDiningRooms(diningRooms)

    override suspend fun addDiningRoom(restaurantId: Int, diningRoom: DiningRoom): Resource<Int> {
        val response = remoteDatasource.insertDiningRoom(restaurantId, diningRoom)
        if (response is Resource.Error)
            return response
        return localDatasource.addDiningRoom(diningRoom)
    }

    override suspend fun getDiningRoom(id: Int): Resource<DiningRoom> =
        localDatasource.getDiningRoom(id)

    override fun getAllDiningRooms(query: String): Flow<Resource<List<DiningRoom>>> =
        localDatasource.getDiningRooms(query)

    override suspend fun fetchDiningRooms(restaurantId: Int): Resource<Int> {
        return when (val response = remoteDatasource.getDiningRooms(restaurantId)) {
            is Resource.Success -> localDatasource.upsertDiningRooms(response.data)
            is Resource.Error -> Resource.Error(response.message)
        }
    }

    override suspend fun updateDiningRoom(restaurantId: Int, diningRoom: DiningRoom): Resource<Int> =
        remoteDatasource.updateDiningRoom(restaurantId, diningRoom)

    override suspend fun deleteDiningRoom(restaurantId: Int, id: Int): Resource<Int> =
        remoteDatasource.deleteDiningRoom(restaurantId, id)
}