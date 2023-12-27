package com.savent.restaurant.data.remote.datasource

import com.savent.restaurant.data.common.model.DiningRoom
import com.savent.restaurant.utils.Resource

interface DiningRoomsRemoteDatasource {

    suspend fun getDiningRooms(restaurantId: Int): Resource<List<DiningRoom>>

    suspend fun insertDiningRoom(restaurantId: Int, diningRoom: DiningRoom): Resource<Int>

    suspend fun updateDiningRoom(restaurantId: Int, diningRoom: DiningRoom): Resource<Int>

    suspend fun deleteDiningRoom(restaurantId: Int, diningRoomId: Int): Resource<Int>

}