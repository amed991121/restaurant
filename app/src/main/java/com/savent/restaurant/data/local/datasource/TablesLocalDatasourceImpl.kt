package com.savent.restaurant.data.local.datasource

import android.util.Log
import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Table
import com.savent.restaurant.data.local.database.dao.TableDao
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class TablesLocalDatasourceImpl(private val tableDao: TableDao) :
    TablesLocalDatasource {

    override suspend fun addTable(table: Table): Resource<Int> =
        withContext(Dispatchers.IO) {
            val result = tableDao.insert(table)
            if (result > 0L)
                return@withContext Resource.Success(result.toInt())
            Resource.Error(Message.StringResource(R.string.add_table_error))
        }

    override suspend fun getTable(id: Int): Resource<Table> =
        withContext(Dispatchers.IO) {
            val result = tableDao.get(id)
            if (result != null)
                return@withContext Resource.Success(result)
            Resource.Error(Message.StringResource(R.string.table_not_found))
        }


    override fun getTables(query: String): Flow<Resource<List<Table>>> = flow {
        tableDao.getAll(query).onEach {
            emit(Resource.Success(it))
        }.collect()
    }

    override suspend fun upsertTables(tables: List<Table>): Resource<Int> =
        synchronized(this) {
            runBlocking(Dispatchers.IO) {
                tableDao.getAll().forEach { current ->
                    if (tables.find { new -> current.id == new.id } == null)
                        tableDao.delete(current)
                }
                val result = tableDao.upsertAll(tables)
                if (result.isEmpty() && tables.isNotEmpty())
                    return@runBlocking Resource.Error<Int>(
                        Message.StringResource(R.string.update_tables_error)
                    )
                Resource.Success(result.size)
            }
        }
}