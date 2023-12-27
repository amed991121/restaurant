package com.savent.restaurant.data.remote.datasource

import com.savent.restaurant.data.remote.model.Order
import com.savent.restaurant.utils.Resource

interface OrdersRemoteDatasource {

    suspend fun getOrders(restaurantId: Int, companyId: Int): Resource<List<Order>>

    suspend fun insertOrder(
        restaurantId: Int,
        companyId: Int,
        employeeId: Int,
        branchId: Int,
        order: Order
    ): Resource<Int>

    suspend fun updateOrder(restaurantId: Int, order: Order): Resource<Int>

    suspend fun deleteOrder(restaurantId: Int, orderId: Int): Resource<Int>
}