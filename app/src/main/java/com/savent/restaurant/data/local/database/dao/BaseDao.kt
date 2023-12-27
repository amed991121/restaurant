package com.savent.restaurant.data.local.database.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
abstract class BaseDao<T> {

    protected abstract fun getTableName(): String

    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(item: T): Long

    @Insert(onConflict = REPLACE)
    abstract suspend fun upsertAll(items: List<T>): List<Long>

    fun getAll(): List<T> =
        query(SimpleSQLiteQuery("SELECT * FROM ${getTableName()}"))

    fun get(id: Int): T? =
        query(
            SimpleSQLiteQuery("SELECT * FROM ${getTableName()} WHERE id = $id")
        ).firstOrNull()

    @RawQuery
    protected abstract fun query(query: SupportSQLiteQuery): List<T>

    @Update
    abstract suspend fun update(item: T): Int

    @Delete
    abstract suspend fun delete(item : T): Int

}