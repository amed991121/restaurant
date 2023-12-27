package com.savent.restaurant.data.local.datasource

import com.savent.restaurant.data.common.model.Diner
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

interface DinersLocalDatasource {

    suspend fun addDiner(diner: Diner): Resource<Int>

    suspend fun getDiner(id: Int): Resource<Diner>

    fun getDiners(query: String): Flow<Resource<List<Diner>>>

    suspend fun upsertDiners(diners: List<Diner>): Resource<Int>

}