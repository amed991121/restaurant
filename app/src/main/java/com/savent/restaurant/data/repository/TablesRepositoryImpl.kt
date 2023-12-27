package com.savent.restaurant.data.repository

import com.savent.restaurant.data.common.model.Table
import com.savent.restaurant.data.local.datasource.TablesLocalDatasource
import com.savent.restaurant.data.remote.datasource.TablesRemoteDatasource
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

class TablesRepositoryImpl(
    private val localDatasource: TablesLocalDatasource,
    private val remoteDatasource: TablesRemoteDatasource,
) : TablesRepository {

    override suspend fun upsertTables(tables: List<Table>): Resource<Int> =
        localDatasource.upsertTables(tables)

    override suspend fun addTable(restaurantId: Int, table: Table): Resource<Int> {
        val response = remoteDatasource.insertTable(restaurantId, table)
        if (response is Resource.Error)
            return response
        return localDatasource.addTable(table)
    }

    override suspend fun getTable(id: Int): Resource<Table> =
        localDatasource.getTable(id)

    override fun getAllTables(query: String): Flow<Resource<List<Table>>> =
        localDatasource.getTables(query)

    override suspend fun fetchTables(restaurantId: Int, companyId: Int): Resource<Int> {
        return when (val response = remoteDatasource.getTables(restaurantId, companyId)) {
            is Resource.Success -> localDatasource.upsertTables(response.data)
            is Resource.Error -> Resource.Error(response.message)
        }
    }

    override suspend fun updateTable(restaurantId: Int, table: Table): Resource<Int> =
        remoteDatasource.updateTable(restaurantId, table)

    override suspend fun deleteTable(restaurantId: Int, id: Int): Resource<Int> =
        remoteDatasource.deleteTable(restaurantId, id)
}