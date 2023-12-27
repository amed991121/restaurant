package com.savent.restaurant.data.local.datasource

import android.util.Log
import com.google.gson.Gson
import com.savent.restaurant.R
import com.savent.restaurant.data.local.database.dao.OrderDao
import com.savent.restaurant.data.local.model.OrderEntity
import com.savent.restaurant.data.local.model.Status
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class OrdersLocalDatasourceImpl(private val orderDao: OrderDao) :
    OrdersLocalDatasource {

    override suspend fun addOrder(order: OrderEntity): Resource<Int> =
        withContext(Dispatchers.IO) {
            val result = orderDao.insert(order)
            if (result > 0L)
                return@withContext Resource.Success(result.toInt())
            Resource.Error(
                Message.StringResource(R.string.add_order_error)
            )
        }


    override suspend fun getOrder(id: Int): Resource<OrderEntity> =
        withContext(Dispatchers.IO) {
            val result = orderDao.get(id)
            if (result != null) return@withContext Resource.Success(result)
            Resource.Error(
                Message.StringResource(R.string.order_not_found)
            )
        }

    override fun getOrderAsync(id: Int): Flow<Resource<OrderEntity>> = flow {
        orderDao.getOrderAsync(id).onEach {
            if (it == null)
                emit(Resource.Error(Message.StringResource(R.string.order_not_found)))
            else emit(Resource.Success(it))
        }.collect()
    }

    override suspend fun getOrderByRemoteId(remoteId: Int): Resource<OrderEntity> =
        withContext(Dispatchers.IO) {
            val result = orderDao.getOrder(remoteId)
            if (result != null) return@withContext Resource.Success(result)
            Resource.Error(
                Message.StringResource(R.string.order_not_found)
            )
        }

    override fun getOrders(): Flow<Resource<List<OrderEntity>>> = flow {
        orderDao.getAllOrders().onEach {
            emit(Resource.Success(it))
        }.catch {
            Resource.Error<List<OrderEntity>>(
                Message.StringResource(R.string.get_orders_error)
            )
        }.collect()
    }

    override suspend fun upsertOrders(orders: List<OrderEntity>): Resource<Int> =
        synchronized(this) {
            runBlocking(Dispatchers.IO) {
                orderDao.getAll().forEach { current ->
                    if (orders.find { new -> current.remoteId == new.remoteId } == null
                        && current.status == Status.CLOSED)
                        orderDao.delete(current)
                }
                val result = orderDao.upsertAll(orders)
                if (result.isEmpty() && orders.isNotEmpty())
                    return@runBlocking Resource.Error<Int>(
                        Message.StringResource(R.string.update_orders_error)
                    )
                Resource.Success(result.size)
            }
        }

    override suspend fun updateOrder(order: OrderEntity): Resource<Int> =
        withContext(Dispatchers.IO) {
            val result = orderDao.update(order)
            if (result > 0)
                return@withContext Resource.Success(result)
            Resource.Error(
                Message.StringResource(R.string.update_order_error)
            )
        }

    override suspend fun deleteOrder(order: OrderEntity): Resource<Int> =
        withContext(Dispatchers.IO) {
            val result = orderDao.delete(order)
            if (result > 0) return@withContext Resource.Success(result)
            Resource.Error(
                Message.StringResource(R.string.delete_order_error)
            )
        }
}