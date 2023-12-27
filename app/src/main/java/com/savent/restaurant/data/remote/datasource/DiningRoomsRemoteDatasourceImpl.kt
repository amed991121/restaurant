package com.savent.restaurant.data.remote.datasource

import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.DiningRoom
import com.savent.restaurant.data.remote.service.DiningRoomsApiService
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource

class DiningRoomsRemoteDatasourceImpl(private val diningRoomsApiService: DiningRoomsApiService) :
    DiningRoomsRemoteDatasource {

    override suspend fun getDiningRooms(restaurantId: Int): Resource<List<DiningRoom>> {
        try {
            val response = diningRoomsApiService.getDiningRooms(restaurantId)
            if (response.isSuccessful)
                return Resource.Success(response.body()!!)
            return Resource.Error(Message.DynamicString(response.errorBody().toString()))
        }catch (e: Exception) {
            return Resource.Error(Message.StringResource(R.string.get_diningrooms_error))
        }
    }

    override suspend fun insertDiningRoom(
        restaurantId: Int,
        diningRoom: DiningRoom,
    ): Resource<Int> {
        try {
            val response = diningRoomsApiService.insertDiningRoom(restaurantId, diningRoom)
            if (response.isSuccessful)
                return Resource.Success(response.body()!!)
            return Resource.Error(Message.DynamicString(response.errorBody().toString()))
        }catch (e: Exception) {
            return Resource.Error(Message.StringResource(R.string.add_diningroom_error))
        }
    }

    override suspend fun updateDiningRoom(
        restaurantId: Int,
        diningRoom: DiningRoom,
    ): Resource<Int> {
        try {
            val response = diningRoomsApiService.updateDiningRoom(restaurantId, diningRoom)
            if (response.isSuccessful)
                return Resource.Success(response.body()!!)
            return Resource.Error(Message.DynamicString(response.errorBody().toString()))
        }catch (e: Exception) {
            return Resource.Error(Message.StringResource(R.string.update_diningroom_error))
        }
    }

    override suspend fun deleteDiningRoom(restaurantId: Int, diningRoomId: Int): Resource<Int> {
        try {
            val response = diningRoomsApiService.deleteDiningRoom(restaurantId, diningRoomId)
            if (response.isSuccessful)
                return Resource.Success(response.body()!!)
            return Resource.Error(Message.DynamicString(response.errorBody().toString()))
        }catch (e: Exception) {
            return Resource.Error(Message.StringResource(R.string.delete_dining_room_error))
        }
    }

}