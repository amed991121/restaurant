package com.savent.restaurant.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.savent.restaurant.data.common.model.Diner
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DinerDao: BaseDao<Diner>() {

    override fun getTableName(): String = "diners"

    @Query("SELECT * FROM diners WHERE name LIKE '%' || :query || '%' OR paternal LIKE '%' || :query || '%' OR maternal LIKE '%' || :query || '%' ORDER BY name ASC")
    abstract fun getAll(query: String): Flow<List<Diner>>

    @Query("DELETE FROM diners")
    abstract suspend fun deleteAll(): Int
}