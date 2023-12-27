package com.savent.restaurant.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.savent.restaurant.data.local.model.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class OrderDao: BaseDao<OrderEntity>() {

    override fun getTableName(): String = "orders"

    @Query("SELECT * FROM orders ORDER BY remote_id DESC")
    abstract fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE remote_id=:remoteId")
    abstract fun getOrder(remoteId: Int): OrderEntity?

    @Query("SELECT * FROM orders WHERE id=:id")
    abstract fun getOrderAsync(id: Int): Flow<OrderEntity?>

    @Query("DELETE FROM orders")
    abstract suspend fun deleteAll(): Int
}