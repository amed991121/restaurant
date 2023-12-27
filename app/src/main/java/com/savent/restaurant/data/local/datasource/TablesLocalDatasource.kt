package com.savent.restaurant.data.local.datasource

import com.savent.restaurant.data.common.model.DiningRoom
import com.savent.restaurant.data.common.model.Table
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

interface TablesLocalDatasource {

    suspend fun addTable(table: Table): Resource<Int>

    suspend fun getTable(id: Int): Resource<Table>

    fun getTables(query: String): Flow<Resource<List<Table>>>

    suspend fun upsertTables(tables: List<Table>): Resource<Int>
}