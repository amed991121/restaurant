package com.savent.restaurant.domain.usecase

import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Dish
import com.savent.restaurant.data.local.model.OrderEntity
import com.savent.restaurant.data.local.model.Status
import com.savent.restaurant.data.repository.DishesRepository
import com.savent.restaurant.data.repository.OrdersRepository
import com.savent.restaurant.utils.DateTimeObj
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.first

class AddDishToOrderUseCase(
    private val ordersRepository: OrdersRepository,
    private val createOrderUseCase: CreateOrderUseCase,
    private val computeOrderUseCase: ComputeOrderUseCase,
) {

    suspend operator fun invoke(
        dishId: Int,
        tablesIdList: List<Int>,
        ordersIdList: List<Int>,
    ): Resource<Int> {
        var ordersEntityList: List<OrderEntity> = mutableListOf()

        when (val result =
            runCatching {
                ordersRepository.getAllOrders().first()
            }.getOrDefault(
                Resource.Success(
                    listOf()
                )
            )) {
            is Resource.Error -> {
                return Resource.Error(result.message)
            }
            is Resource.Success -> {
                ordersEntityList = result.data.filter { it.status == Status.OPEN }
            }
        }

        try {
            ordersEntityList.forEach { order ->
                ordersIdList.forEach { orderId ->
                    if (order.id == orderId) {
                        order.dishes[dishId] = order.dishes.getOrDefault(dishId, 0) + 1
                        val orderUpdated = ordersRepository.updateOrder(order)
                        if (orderUpdated is Resource.Error) {
                            throw Exception()
                        }
                        computeOrderUseCase(order)
                    }
                }
            }
            tablesIdList.forEach forEach@{ tableId ->

                ordersEntityList.find { it.tableId == tableId }?.let { order ->
                    order.dishes[dishId] = order.dishes.getOrDefault(dishId, 0) + 1
                    val orderUpdated = ordersRepository.updateOrder(order)
                    if (orderUpdated is Resource.Error) {
                        throw Exception()
                    }
                    computeOrderUseCase(order)
                    return@forEach
                }


                if (createOrderUseCase(
                        dishes = hashMapOf(Pair(dishId, 1)),
                        tableId = tableId
                    ) is Resource.Error
                ) {
                    throw Exception()
                }
            }
        } catch (e: Exception) {
            return Resource.Error(Message.StringResource(R.string.add_dish_error))
        }
        return Resource.Success(0)
    }
}