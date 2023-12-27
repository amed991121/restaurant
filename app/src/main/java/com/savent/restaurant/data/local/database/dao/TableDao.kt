package com.savent.restaurant.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.savent.restaurant.data.common.model.Table
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TableDao: BaseDao<Table>() {

    override fun getTableName(): String = "tables"

    @Query("SELECT * FROM tables WHERE name LIKE '%' || :query || '%' ORDER BY id ASC")
    abstract fun getAll(query: String): Flow<List<Table>>

    @Query("DELETE FROM tables")
    abstract suspend fun deleteAll(): Int
}