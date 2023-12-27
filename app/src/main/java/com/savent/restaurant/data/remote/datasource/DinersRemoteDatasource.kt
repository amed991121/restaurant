package com.savent.restaurant.data.remote.datasource

import com.savent.restaurant.data.common.model.Diner
import com.savent.restaurant.utils.Resource

interface DinersRemoteDatasource {

    suspend fun getDiners(restaurantId: Int, companyId: Int): Resource<List<Diner>>

    suspend fun insertDiner(restaurantId: Int, diner: Diner): Resource<Int>

    suspend fun updateDiner(restaurantId: Int, diner: Diner): Resource<Int>

    suspend fun deleteDiner(restaurantId: Int, dinerId: Int): Resource<Int>
}