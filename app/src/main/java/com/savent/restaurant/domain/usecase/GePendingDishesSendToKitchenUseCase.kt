package com.savent.restaurant.domain.usecase

import android.util.Log
import com.savent.restaurant.data.repository.DishesRepository
import com.savent.restaurant.data.repository.KitchenNotesRepository
import com.savent.restaurant.data.repository.OrdersRepository
import com.savent.restaurant.ui.model.dish.DishModel
import com.savent.restaurant.ui.model.dish.toModel
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.*

class GePendingDishesSendToKitchenUseCase(
    private val ordersRepository: OrdersRepository,
    private val kitchenNotesRepository: KitchenNotesRepository,
    private val dishesRepository: DishesRepository
) {

    operator fun invoke(orderId: Int): Flow<Resource<List<DishModel>>> = flow {
        kitchenNotesRepository.getNote(orderId).combine(ordersRepository.getOrderAsync(orderId)){ kitchenNote, orderResult->

            val order = orderResult.let {
                if (it is Resource.Error) {
                    emit(Resource.Error(it.message))
                    return@combine
                }
                (it as Resource.Success).data
            }

            val dishes = dishesRepository.getDishes(order.dishes.keys.toList()).let {
                if (it is Resource.Error) {
                    emit(Resource.Error(it.message))
                    return@combine
                }
                (it as Resource.Success).data
            }.filterNot { dish ->
                kitchenNote?.dishesSent?.get(dish.id) == order.dishes[dish.id]
            }.map {
                it.toModel().copy(
                    units = (order.dishes.getOrDefault(
                        it.id,
                        0
                    ) - (kitchenNote?.dishesSent?.get(it.id) ?: 0))
                )
            }
            emit(Resource.Success(dishes))
            
        }.collect()
    }
}