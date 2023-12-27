package com.savent.restaurant.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.savent.restaurant.data.local.model.KitchenNote
import kotlinx.coroutines.flow.Flow

@Dao
interface KitchenNoteDao {

    @Insert(onConflict = REPLACE)
    suspend fun upsertNote(note: KitchenNote): Long

    @Query("SELECT * FROM kitchen_notes WHERE order_id=:orderId")
    fun getNote(orderId: Int): Flow<KitchenNote?>

    @Query("DELETE FROM kitchen_notes WHERE order_id=:orderId")
    suspend fun deleteNote(orderId: Int): Int
}