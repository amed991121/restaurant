package com.savent.restaurant.domain.usecase

import android.util.Log
import com.savent.restaurant.data.common.model.Dish
import com.savent.restaurant.data.common.model.Table
import com.savent.restaurant.data.local.model.OrderEntity
import com.savent.restaurant.data.repository.DishesRepository
import com.savent.restaurant.data.repository.OrdersRepository
import com.savent.restaurant.data.repository.TablesRepository
import com.savent.restaurant.ui.model.CheckoutModel
import com.savent.restaurant.ui.model.OrderModel
import com.savent.restaurant.ui.model.dish.DishModel
import com.savent.restaurant.ui.model.dish.toModel
import com.savent.restaurant.utils.DecimalFormat
import com.savent.restaurant.utils.NameFormat
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.*

class GetOpenOrderUseCase(
    private val ordersRepository: OrdersRepository,
    private val tablesRepository: TablesRepository,
    private val dishesRepository: DishesRepository,
    private val getPendingDishesSendToKitchenUseCase: GePendingDishesSendToKitchenUseCase
) {

    operator fun invoke(orderId: Int): Flow<Resource<OrderModel>> = flow {
        ordersRepository.getOrderAsync(orderId).onEach { result ->
            var order: OrderEntity? = null
            when (result) {
                is Resource.Error -> {
                    emit(Resource.Error(result.message))
                    return@onEach
                }
                is Resource.Success -> {
                    order = result.data
                }
            }

            val paymentMethod = PaymentMethodUseCase.get(order.paymentMethod)

            var table: Table? = null

            when (val resultTable = tablesRepository.getTable(order.tableId)) {
                is Resource.Error -> {
                    emit(Resource.Error(resultTable.message))
                    return@onEach
                }
                is Resource.Success -> {
                    table = resultTable.data
                }
            }

            var dishes: List<DishModel>? = null
            when (val resultDishes = dishesRepository.getDishes(order.dishes.keys.toList())) {
                is Resource.Error -> {
                    emit(Resource.Error(resultDishes.message))
                    return@onEach
                }
                is Resource.Success -> dishes = resultDishes.data.map {
                    var dish = it.toModel()
                    dish = dish.copy(units = order.dishes[dish.id] ?: 0)
                    dish
                }
            }
            val pendingDishesSendToKitchen =
                getPendingDishesSendToKitchenUseCase(orderId).first().let {
                    if (it is Resource.Error) {
                        emit(Resource.Error(it.message))
                        return@onEach
                    }
                    (it as Resource.Success).data
                }

            emit(
                Resource.Success(
                    OrderModel(
                        tableName = NameFormat.format(table.name),
                        tag = NameFormat.format(order.tag),
                        dishes = dishes,
                        paymentMethod = paymentMethod,
                        checkout = CheckoutModel(
                            subtotal = DecimalFormat.format(order.subtotal),
                            discounts = DecimalFormat.format(order.discounts),
                            total = DecimalFormat.format(order.total),
                            collected = DecimalFormat.format(order.collected),
                            change = DecimalFormat.format(
                                (order.collected - order.total)
                                    .let { if (it < 0) 0F else it }
                            )
                        ),
                        pendingKitchenDishes = pendingDishesSendToKitchen
                    )
                )
            )
        }.collect()
    }
}