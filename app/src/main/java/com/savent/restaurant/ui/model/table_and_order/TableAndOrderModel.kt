package com.savent.restaurant.ui.model.table_and_order

data class TableAndOrderModel(
    val tableId: Int,
    val tableName: String,
    val totalOrders: Int = 0,
    val orderNames: List<OrderNameModel> = listOf(),
    val isSelected: Boolean = false
)

