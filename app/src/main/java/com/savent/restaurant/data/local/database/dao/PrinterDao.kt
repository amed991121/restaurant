package com.savent.restaurant.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.savent.restaurant.data.local.model.Printer


@Dao
interface PrinterDao {

    @Insert(onConflict = REPLACE)
    suspend fun upsertPrinter(printer: Printer): Long

    @Query("SELECT * FROM printers WHERE location=:location LIMIT 1")
    suspend fun getPrinter(location: Printer.Location): Printer?

    @Query("DELETE FROM printers WHERE location=:location")
    suspend fun deletePrinter(location: Printer.Location): Int

}