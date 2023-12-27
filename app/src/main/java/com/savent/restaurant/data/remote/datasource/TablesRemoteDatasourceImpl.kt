package com.savent.restaurant.data.remote.datasource

import com.google.gson.Gson
import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Table
import com.savent.restaurant.data.remote.ErrorBody
import com.savent.restaurant.data.remote.service.TablesApiService
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource

class TablesRemoteDatasourceImpl(private val tablesApiService: TablesApiService) :
    TablesRemoteDatasource {

    override suspend fun getTables(restaurantId: Int, companyId: Int): Resource<List<Table>> {
        try {
            val response = tablesApiService.getTables(restaurantId, companyId)
            if (response.isSuccessful)
                return Resource.Success(response.body()!!)
            return Resource.Error(Message.DynamicString(
                Gson().fromJson(
                    response.errorBody()?.charStream(),
                    ErrorBody::class.java
                ).message
            ))
        } catch (e: Exception) {
            return Resource.Error(Message.StringResource(R.string.fetch_tables_error))
        }
    }

    override suspend fun insertTable(restaurantId: Int, table: Table): Resource<Int> {
        try {
            val response = tablesApiService.insertTable(restaurantId, table)
            if (response.isSuccessful)
                return Resource.Success(response.body()!!)
            return Resource.Error(Message.DynamicString(response.errorBody().toString()))
        } catch (e: Exception) {
            return Resource.Error(Message.StringResource(R.string.add_table_error))
        }
    }

    override suspend fun updateTable(restaurantId: Int, table: Table): Resource<Int> {
        try {
            val response = tablesApiService.updateTable(restaurantId, table)
            if (response.isSuccessful)
                return Resource.Success(response.body()!!)
            return Resource.Error(Message.DynamicString(response.errorBody().toString()))
        } catch (e: Exception) {
            return Resource.Error(Message.StringResource(R.string.update_table_error))
        }
    }

    override suspend fun deleteTable(restaurantId: Int, tableId: Int): Resource<Int> {
        try {
            val response = tablesApiService.deleteTable(restaurantId, tableId)
            if (response.isSuccessful)
                return Resource.Success(response.body()!!)
            return Resource.Error(Message.DynamicString(response.errorBody().toString()))
        } catch (e: Exception) {
            return Resource.Error(Message.StringResource(R.string.delete_table_error))
        }
    }

}