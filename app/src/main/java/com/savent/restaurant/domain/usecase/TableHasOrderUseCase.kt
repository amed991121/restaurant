package com.savent.restaurant.domain.usecase

import com.savent.restaurant.data.local.model.OrderEntity
import com.savent.restaurant.data.local.model.Status
import com.savent.restaurant.data.repository.OrdersRepository
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.first

class TableHasOrderUseCase(
    private val ordersRepository: OrdersRepository,
) {
    suspend operator fun invoke(tableId: Int): Resource<Boolean> {
        when (val result =
            runCatching {
                ordersRepository.getAllOrders(
                ).first()
            }.getOrDefault(
                Resource.Success(
                    listOf()
                )
            )) {
            is Resource.Success -> result.data.forEach {
                if (it.status == Status.OPEN && it.tableId == tableId)
                    return Resource.Success(true)
            }
            is Resource.Error -> return Resource.Error(result.message)

        }
        return Resource.Success(false)
    }
}