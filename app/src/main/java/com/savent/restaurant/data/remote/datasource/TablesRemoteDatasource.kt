package com.savent.restaurant.data.remote.datasource

import com.savent.restaurant.data.common.model.Table
import com.savent.restaurant.utils.Resource

interface TablesRemoteDatasource {

    suspend fun getTables(restaurantId: Int, companyId: Int): Resource<List<Table>>

    suspend fun insertTable(restaurantId: Int, table: Table): Resource<Int>

    suspend fun updateTable(restaurantId: Int, table: Table): Resource<Int>

    suspend fun deleteTable(restaurantId: Int, tableId: Int): Resource<Int>

}