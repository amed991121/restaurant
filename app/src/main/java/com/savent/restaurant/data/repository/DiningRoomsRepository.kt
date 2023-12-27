package com.savent.restaurant.data.repository

import com.savent.restaurant.data.common.model.DiningRoom
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

interface DiningRoomsRepository {

    suspend fun upsertDiningRooms(diningRooms: List<DiningRoom>): Resource<Int>

    suspend fun addDiningRoom(restaurantId: Int, diningRoom: DiningRoom): Resource<Int>

    suspend fun getDiningRoom(id: Int): Resource<DiningRoom>

    fun getAllDiningRooms(query: String): Flow<Resource<List<DiningRoom>>>

    suspend fun fetchDiningRooms(restaurantId: Int): Resource<Int>

    suspend fun updateDiningRoom(restaurantId: Int, diningRoom: DiningRoom): Resource<Int>

    suspend fun deleteDiningRoom(restaurantId: Int, id: Int): Resource<Int>
}