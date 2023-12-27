package com.savent.restaurant.data.local.datasource

import com.savent.restaurant.data.common.model.DiningRoom
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

interface DiningRoomsLocalDatasource {

    suspend fun addDiningRoom(diningRoom: DiningRoom): Resource<Int>

    suspend fun getDiningRoom(id: Int): Resource<DiningRoom>

    fun getDiningRooms(query: String): Flow<Resource<List<DiningRoom>>>

    suspend fun upsertDiningRooms(diningRooms: List<DiningRoom>): Resource<Int>
}