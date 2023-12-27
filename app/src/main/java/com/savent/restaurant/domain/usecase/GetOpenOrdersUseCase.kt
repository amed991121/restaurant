package com.savent.restaurant.domain.usecase

import android.util.Log
import com.savent.restaurant.data.common.model.Table
import com.savent.restaurant.data.local.model.Status
import com.savent.restaurant.data.repository.OrdersRepository
import com.savent.restaurant.data.repository.TablesRepository
import com.savent.restaurant.ui.model.table_and_order.OrderNameModel
import com.savent.restaurant.ui.model.table_and_order.TableAndOrderModel
import com.savent.restaurant.utils.NameFormat
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.*

class GetOpenOrdersUseCase(
    private val ordersRepository: OrdersRepository,
    private val tablesRepository: TablesRepository,
) {

    operator fun invoke(query: String): Flow<Resource<List<TableAndOrderModel>>> = flow {
        ordersRepository.getAllOrders().onEach { result ->
            var tablesList: List<Table> = mutableListOf()
            when (val resultTable =
                runCatching {
                    tablesRepository.getAllTables(query).first()
                }.getOrDefault(
                    Resource.Success(
                        listOf()
                    )
                )) {
                is Resource.Error -> {
                    emit(Resource.Error(resultTable.message))
                }
                is Resource.Success -> {
                    tablesList = resultTable.data
                }
            }
            val ordersList = mutableListOf<TableAndOrderModel>()
            when (result) {
                is Resource.Error -> {
                    emit(Resource.Error(result.message))
                    return@onEach
                }
                is Resource.Success -> {
                    val openOrders = result.data.filter { it.status == Status.OPEN }
                    tablesList.forEach { table ->
                        var totalOrders = 0
                        val orderNames = mutableListOf<OrderNameModel>()
                        openOrders.forEach { order ->
                            if (table.id == order.tableId) {
                                totalOrders++
                                orderNames.add(
                                    OrderNameModel(order.id, NameFormat.format(order.tag))
                                )
                            }
                        }
                        if (totalOrders > 0)
                            ordersList.add(
                                TableAndOrderModel(
                                    table.id,
                                    NameFormat.format(table.name),
                                    totalOrders,
                                    orderNames
                                )
                            )
                    }
                    emit(Resource.Success(ordersList))
                }
            }

        }.collect()


    }
}