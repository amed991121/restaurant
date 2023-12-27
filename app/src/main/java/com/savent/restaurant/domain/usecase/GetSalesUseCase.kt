package com.savent.restaurant.domain.usecase

import com.savent.restaurant.data.local.model.Status
import com.savent.restaurant.data.repository.OrdersRepository
import com.savent.restaurant.data.repository.TablesRepository
import com.savent.restaurant.isToday
import com.savent.restaurant.toLong
import com.savent.restaurant.ui.model.CheckoutModel
import com.savent.restaurant.ui.model.sale.SaleModel
import com.savent.restaurant.utils.DateFormat
import com.savent.restaurant.utils.DecimalFormat
import com.savent.restaurant.utils.NameFormat
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class GetSalesUseCase(
    private val ordersRepository: OrdersRepository,
    private val tablesRepository: TablesRepository,
) {

    operator fun invoke(query: String): Flow<Resource<List<SaleModel>>> = flow {

        ordersRepository.getAllOrders().onEach { result ->
            when (result) {
                is Resource.Error -> {
                    emit(Resource.Error(result.message))
                    return@onEach
                }
                is Resource.Success -> {
                    val sales = mutableListOf<SaleModel>()
                    result.data.forEach { order ->
                        if (order.status == Status.OPEN || order.tableId == 0) return@forEach
                        if(!order.date_timestamp.isToday()) return@onEach
                        val tableName =
                            when (val tableResult = tablesRepository.getTable(order.tableId)) {
                                is Resource.Error -> "No Definido"
                                is Resource.Success -> NameFormat.format(tableResult.data.name)
                            }
                        if(!tableName.contains(query,ignoreCase = true)) return@forEach

                        val paymentMethod = PaymentMethodUseCase.get(order.paymentMethod)
                        sales.add(
                            SaleModel(
                                id = order.remoteId,
                                tableName = tableName,
                                totalDishes = order.dishes.size,
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
                                DateFormat.format(order.date_timestamp.toLong(), "hh:mm a")
                            )
                        )
                    }
                    emit(Resource.Success(sales))
                }
            }
        }.collect()
    }
}