package com.savent.restaurant.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.savent.restaurant.data.common.model.Dish
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DishDao : BaseDao<Dish>() {

    override fun getTableName(): String = "dishes"

    @Query("SELECT * FROM dishes WHERE name LIKE '%' || :query || '%' AND category=:category ORDER BY name ASC")
    abstract fun getAllWithFilter(query: String, category: Dish.Category): Flow<List<Dish>>

    @Query("SELECT * FROM dishes WHERE name LIKE '%' || :query || '%' OR price LIKE '%' || :query || '%' ORDER BY name ASC")
    abstract fun getAll(query: String): Flow<List<Dish>>

    @Query("SELECT * FROM dishes WHERE id IN (:listId)")
    abstract fun getDishes(listId: List<Int>):List<Dish>
}