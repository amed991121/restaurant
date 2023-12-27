package com.savent.restaurant.data.remote.service


import com.savent.restaurant.data.common.model.DiningRoom
import retrofit2.Response
import retrofit2.http.*

interface DiningRoomsApiService {

    @GET
    suspend fun getDiningRooms(
        @Query("restaurantId") restaurantId: Int
    ): Response<List<DiningRoom>>

    @POST
    suspend fun insertDiningRoom(
        @Query("restaurantId") restaurantId: Int,
        @Body diningRoom: DiningRoom
    ): Response<Int>

    @PUT
    suspend fun updateDiningRoom(
        @Query("restaurantId") restaurantId: Int,
        @Body diningRoom: DiningRoom
    ): Response<Int>

    @DELETE
    suspend fun deleteDiningRoom(
        @Query("restaurantId") restaurantId: Int,
        @Query("diningRoomId") diningRoomId: Int
    ): Response<Int>
}