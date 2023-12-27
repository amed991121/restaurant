package com.savent.restaurant.data.remote.datasource

import android.util.Log
import com.google.gson.Gson
import com.savent.restaurant.R
import com.savent.restaurant.data.remote.ErrorBody
import com.savent.restaurant.data.remote.model.Order
import com.savent.restaurant.data.remote.service.OrdersApiService
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrdersRemoteDatasourceImpl(private val ordersApiService: OrdersApiService) :
    OrdersRemoteDatasource {

    override suspend fun getOrders(restaurantId: Int, companyId: Int): Resource<List<Order>> =
        withContext(Dispatchers.IO) {
            try {
                val response = ordersApiService.getOrders(
                    restaurantId = restaurantId,
                    companyId = companyId
                )
                //Log.d("log_","orderreponse${response.message()}//${response.errorBody()}//${response.raw()}")
                if (response.isSuccessful)
                    return@withContext Resource.Success(response.body()!!)
                Resource.Error(
                    Message.DynamicString(
                        Gson().fromJson(
                            response.errorBody()?.charStream(),
                            ErrorBody::class.java
                        ).message
                    )
                )
            } catch (e: Exception) {
                //Log.d("log_","orderexception${e.message}//${e.cause}")
                Resource.Error(Message.StringResource(R.string.get_orders_error))
            }
        }

    override suspend fun insertOrder(
        restaurantId: Int,
        companyId: Int,
        employeeId: Int,
        branchId: Int,
        order: Order
    ): Resource<Int> =
        withContext(Dispatchers.IO) {
            try {
                val response = ordersApiService.insertOrder(
                    restaurantId = restaurantId,
                    companyId = companyId,
                    employeeId = employeeId,
                    branchId = branchId,
                    order = order
                )

                if (response.isSuccessful)
                    return@withContext Resource.Success(response.body()!!)
                Resource.Error(
                    Message.DynamicString(
                        Gson().fromJson(
                            response.errorBody()?.charStream(),
                            ErrorBody::class.java
                        ).message
                    )
                )
            } catch (e: Exception) {
                Resource.Error(Message.StringResource(R.string.register_order_error))
            }
        }

    override suspend fun updateOrder(restaurantId: Int, order: Order): Resource<Int> {
        try {
            val response = ordersApiService.updateOrder(restaurantId, order)
            if (response.isSuccessful)
                return Resource.Success(response.body()!!)
            return Resource.Error(Message.DynamicString(response.errorBody().toString()))
        } catch (e: Exception) {
            return Resource.Error(Message.StringResource(R.string.update_order_error))
        }
    }

    override suspend fun deleteOrder(restaurantId: Int, orderId: Int): Resource<Int> {
        try {
            val response = ordersApiService.deleteOrder(restaurantId, orderId)
            if (response.isSuccessful)
                return Resource.Success(response.body()!!)
            return Resource.Error(Message.DynamicString(response.errorBody().toString()))
        } catch (e: Exception) {
            return Resource.Error(Message.StringResource(R.string.delete_order_error))
        }
    }
}