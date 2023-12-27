package com.savent.restaurant.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.savent.restaurant.data.common.model.DiningRoom
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DiningRoomDao: BaseDao<DiningRoom>() {

    override fun getTableName(): String = "dining_rooms"

    @Query("SELECT * FROM dining_rooms WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    abstract fun getAll(query: String): Flow<List<DiningRoom>>

    @Query("DELETE FROM dining_rooms")
    abstract suspend fun deleteAll(): Int
}