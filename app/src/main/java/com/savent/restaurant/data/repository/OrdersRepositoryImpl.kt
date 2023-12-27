package com.savent.restaurant.data.repository

import android.util.Log
import com.google.gson.Gson
import com.savent.restaurant.data.local.datasource.OrdersLocalDatasource
import com.savent.restaurant.data.local.model.OrderEntity
import com.savent.restaurant.data.local.model.Status
import com.savent.restaurant.data.local.model.toLocal
import com.savent.restaurant.data.remote.datasource.OrdersRemoteDatasource
import com.savent.restaurant.data.remote.model.toNetwork
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

class OrdersRepositoryImpl(
    private val localDatasource: OrdersLocalDatasource,
    private val remoteDatasource: OrdersRemoteDatasource,
) : OrdersRepository {

    override suspend fun upsertOrders(orders: List<OrderEntity>): Resource<Int> =
        localDatasource.upsertOrders(orders)

    override suspend fun createOrder(order: OrderEntity): Resource<Int> =
        localDatasource.addOrder(order)

    override suspend fun saveOrder(
        localId: Int,
        restaurantId: Int,
        companyId: Int,
        employeeId: Int,
        branchId: Int
    ): Resource<Int> {
        var orderEntity: OrderEntity? = null
        when (val result = localDatasource.getOrder(localId)) {
            is Resource.Error -> return Resource.Error(result.message)
            is Resource.Success -> {
                orderEntity = result.data
            }
        }

        var orderId = 0
        when (val result = remoteDatasource.insertOrder(
            restaurantId = restaurantId,
            companyId = companyId,
            employeeId = employeeId,
            branchId = branchId,
            order = orderEntity.toNetwork()
        )) {
            is Resource.Error -> return Resource.Error(result.message)
            is Resource.Success -> orderId = result.data
        }

        return localDatasource.updateOrder(
            orderEntity.copy(
                remoteId = orderId,
                status = Status.CLOSED
            )
        )

    }

    override suspend fun getOrder(id: Int): Resource<OrderEntity> =
        localDatasource.getOrder(id)

    override fun getOrderAsync(id: Int): Flow<Resource<OrderEntity>> =
        localDatasource.getOrderAsync(id)

    override suspend fun getOrderByRemoteId(remoteId: Int): Resource<OrderEntity> =
        localDatasource.getOrderByRemoteId(remoteId)

    override fun getAllOrders(): Flow<Resource<List<OrderEntity>>> =
        localDatasource.getOrders()

    override suspend fun fetchOrders(restaurantId: Int, companyId: Int): Resource<Int> {
        return when (val response = remoteDatasource.getOrders(restaurantId, companyId)) {
            is Resource.Success -> {
                localDatasource.upsertOrders(response.data.map { it.toLocal() })
            }
            is Resource.Error -> Resource.Error(response.message)
        }
    }

    override suspend fun updateOrder(order: OrderEntity): Resource<Int> {
        return localDatasource.updateOrder(order)
    }

    override suspend fun deleteOrder(id: Int): Resource<Int> {
        return when (val result = localDatasource.getOrder(id)) {
            is Resource.Success -> localDatasource.deleteOrder(result.data)
            is Resource.Error -> Resource.Error(result.message)
        }
    }
}