package com.savent.restaurant.domain.usecase

import com.savent.restaurant.data.common.model.Dish
import com.savent.restaurant.data.local.model.OrderEntity
import com.savent.restaurant.data.local.model.Status
import com.savent.restaurant.data.repository.DishesRepository
import com.savent.restaurant.data.repository.OrdersRepository
import com.savent.restaurant.utils.DateTimeObj
import com.savent.restaurant.utils.Resource

class CreateOrderUseCase(
    private val ordersRepository: OrdersRepository,
    private val dishesRepository: DishesRepository
) {

    suspend operator fun invoke(
        tableId: Int,
        dishes: HashMap<Int, Int> = HashMap(),
        dinerId: Int = 0,
        tag: String? = null
    ): Resource<Int> {
        var dishesObj = listOf<Dish>()
        when (val result = dishesRepository.getDishes(
            dishes.keys.toList()
        )) {
            is Resource.Success -> dishesObj = result.data
            is Resource.Error -> return Resource.Error(result.message)

        }

        var totalAmount = 0F
        dishesObj.forEach { totalAmount += it.price }

        return ordersRepository.createOrder(
            OrderEntity(
                id = 0,
                remoteId = Int.MAX_VALUE,
                dinerId = dinerId,
                tableId = tableId,
                status = Status.OPEN,
                dishes = dishes,
                subtotal = totalAmount,
                discounts = 0F,
                total = totalAmount,
                collected = 0F,
                tip = 0F,
                tag = tag?:"Order #1",
                date_timestamp = DateTimeObj.fromLong(System.currentTimeMillis()),
            )
        )
    }
}