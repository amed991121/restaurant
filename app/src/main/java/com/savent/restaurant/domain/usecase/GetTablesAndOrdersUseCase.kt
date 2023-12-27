package com.savent.restaurant.domain.usecase

import com.savent.restaurant.data.local.model.OrderEntity
import com.savent.restaurant.data.local.model.Status
import com.savent.restaurant.data.repository.OrdersRepository
import com.savent.restaurant.data.repository.TablesRepository
import com.savent.restaurant.ui.model.table_and_order.OrderNameModel
import com.savent.restaurant.ui.model.table_and_order.TableAndOrderModel
import com.savent.restaurant.utils.NameFormat
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.*

class GetTablesAndOrdersUseCase(
    private val ordersRepository: OrdersRepository,
    private val tablesRepository: TablesRepository,
) {

    operator fun invoke(query: String): Flow<Resource<List<TableAndOrderModel>>> = flow {
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
                emit(Resource.Error(result.message))
            }
            is Resource.Success -> {
                ordersEntityList = result.data.filter { it.status == Status.OPEN }
            }
        }
        tablesRepository.getAllTables(query).onEach { result ->
            val tableOrdersList = mutableListOf<TableAndOrderModel>()
            when (result) {
                is Resource.Error -> {
                    emit(Resource.Error(result.message))
                    return@onEach
                }
                is Resource.Success -> {
                    result.data.forEach { table ->
                        var totalOrders = 0
                        val orderNames = mutableListOf<OrderNameModel>()
                        ordersEntityList.forEach { order ->
                            if (table.id == order.tableId) {
                                totalOrders++
                                orderNames.add(
                                    OrderNameModel(order.id, NameFormat.format(order.tag))
                                )
                            }
                        }
                        tableOrdersList.add(
                            TableAndOrderModel(
                                table.id,
                                NameFormat.format(table.name),
                                totalOrders,
                                orderNames
                            )
                        )
                    }
                    emit(Resource.Success(tableOrdersList))
                }
            }
        }.collect()
    }
}