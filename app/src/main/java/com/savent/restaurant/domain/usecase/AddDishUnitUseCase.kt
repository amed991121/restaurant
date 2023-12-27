package com.savent.restaurant.domain.usecase

import com.savent.restaurant.data.local.model.OrderEntity
import com.savent.restaurant.data.repository.OrdersRepository
import com.savent.restaurant.utils.Resource

class AddDishUnitUseCase (
    private val ordersRepository: OrdersRepository,
    private val computeOrderUseCase: ComputeOrderUseCase
) {

    suspend operator fun invoke(dishId: Int, orderId: Int): Resource<Int> {

        var order: OrderEntity? = null
        when(val result = ordersRepository.getOrder(orderId)){
            is Resource.Error -> return Resource.Error(result.message)
            is Resource.Success -> order = result.data
        }

        order.dishes[dishId] = order.dishes.getOrDefault(dishId, 0) + 1

        return when(val result = ordersRepository.updateOrder(order)) {
            is Resource.Error -> Resource.Error(result.message)
            is Resource.Success -> computeOrderUseCase(order)
        }
    }
}