package com.savent.restaurant.data.repository

import com.savent.restaurant.data.common.model.Diner
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

interface DinersRepository {

    suspend fun upsertDiners(diners: List<Diner>): Resource<Int>

    suspend fun addDiner(restaurantId: Int, diner: Diner): Resource<Int>

    suspend fun getDiner(id: Int): Resource<Diner>

    fun getAllDiners(query: String): Flow<Resource<List<Diner>>>

    suspend fun fetchDiners(restaurantId: Int, companyId: Int): Resource<Int>

    suspend fun updateDiner(restaurantId: Int, diner: Diner): Resource<Int>

    suspend fun deleteDiner(restaurantId: Int, id: Int): Resource<Int>
}