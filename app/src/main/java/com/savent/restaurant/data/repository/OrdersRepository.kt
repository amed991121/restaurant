package com.savent.restaurant.data.repository

import com.savent.restaurant.data.local.model.OrderEntity
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

interface OrdersRepository {

    suspend fun upsertOrders(orders: List<OrderEntity>): Resource<Int>

    suspend fun createOrder(order: OrderEntity): Resource<Int>

    suspend fun saveOrder(
        localId: Int,
        restaurantId: Int,
        companyId: Int,
        employeeId: Int,
        branchId: Int
    ): Resource<Int>

    suspend fun getOrder(id: Int): Resource<OrderEntity>

    fun getOrderAsync(id: Int): Flow<Resource<OrderEntity>>

    suspend fun getOrderByRemoteId(remoteId: Int): Resource<OrderEntity>

    fun getAllOrders(): Flow<Resource<List<OrderEntity>>>

    suspend fun fetchOrders(restaurantId: Int, companyId: Int): Resource<Int>

    suspend fun updateOrder(order: OrderEntity): Resource<Int>

    suspend fun deleteOrder(id: Int): Resource<Int>
}