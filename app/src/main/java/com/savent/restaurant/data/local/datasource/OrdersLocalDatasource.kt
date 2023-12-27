package com.savent.restaurant.data.local.datasource

import com.savent.restaurant.data.local.model.OrderEntity
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

interface OrdersLocalDatasource {

    suspend fun addOrder(order: OrderEntity): Resource<Int>

    suspend fun getOrder(id: Int): Resource<OrderEntity>

    fun getOrderAsync(id: Int): Flow<Resource<OrderEntity>>

    suspend fun getOrderByRemoteId(remoteId: Int): Resource<OrderEntity>

    fun getOrders(): Flow<Resource<List<OrderEntity>>>

    suspend fun upsertOrders(orders: List<OrderEntity>): Resource<Int>

    suspend fun updateOrder(order: OrderEntity): Resource<Int>

    suspend fun deleteOrder(order: OrderEntity): Resource<Int>
}