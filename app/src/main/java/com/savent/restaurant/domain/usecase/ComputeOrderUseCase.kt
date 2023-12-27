package com.savent.restaurant.domain.usecase

import com.savent.restaurant.data.common.model.Dish
import com.savent.restaurant.data.local.model.OrderEntity
import com.savent.restaurant.data.repository.DishesRepository
import com.savent.restaurant.data.repository.OrdersRepository
import com.savent.restaurant.utils.Resource

class ComputeOrderUseCase(
    private val ordersRepository: OrdersRepository,
    private val dishesRepository: DishesRepository
) {

    suspend operator fun invoke(order: OrderEntity): Resource<Int> {
        var newSubtotal = 0F
        var dishes = listOf<Dish>()
        when(val result = dishesRepository.getDishes(order.dishes.keys.toList())){
            is Resource.Error -> return Resource.Error(result.message)
            is Resource.Success -> dishes = result.data
        }
        dishes.forEach {dish->
            newSubtotal += dish.price * (order.dishes[dish.id]?:1)
        }
        val total = newSubtotal - order.discounts
        return ordersRepository.updateOrder(
            order.copy(
                subtotal = newSubtotal,
                total = total
            )
        )
    }
}