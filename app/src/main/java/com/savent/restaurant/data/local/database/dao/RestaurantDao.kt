package com.savent.restaurant.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.savent.restaurant.data.common.model.Restaurant
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RestaurantDao : BaseDao<Restaurant>() {
    override fun getTableName(): String = "restaurants"

    @Query("SELECT * FROM restaurants WHERE id=:id AND company_id=:companyId ORDER BY name ASC")
    abstract fun get(id: Int, companyId: Int): Restaurant?

    @Query("SELECT * FROM restaurants WHERE name LIKE '%' || :query || '%'  AND company_id=:companyId ORDER BY name ASC")
    abstract fun getAll(query: String, companyId: Int): Flow<List<Restaurant>>

}