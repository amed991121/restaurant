package com.savent.restaurant.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.savent.restaurant.data.common.model.Company
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CompanyDao: BaseDao<Company>() {

    override fun getTableName(): String = "companies"

    @Query("SELECT * FROM companies WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    abstract fun getAll(query: String): Flow<List<Company>>

}