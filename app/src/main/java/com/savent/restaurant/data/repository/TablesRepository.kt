package com.savent.restaurant.data.repository

import com.savent.restaurant.data.common.model.Diner
import com.savent.restaurant.data.common.model.Table
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

interface TablesRepository {

    suspend fun upsertTables(tables: List<Table>): Resource<Int>

    suspend fun addTable(restaurantId: Int, table: Table): Resource<Int>

    suspend fun getTable(id: Int): Resource<Table>

    fun getAllTables(query: String): Flow<Resource<List<Table>>>

    suspend fun fetchTables(restaurantId: Int, companyId: Int): Resource<Int>

    suspend fun updateTable(restaurantId: Int, table: Table): Resource<Int>

    suspend fun deleteTable(restaurantId: Int, id: Int): Resource<Int>
}