package com.savent.restaurant.ui.screen.orders

import com.savent.restaurant.data.common.model.Diner
import com.savent.restaurant.data.common.model.Table
import com.savent.restaurant.ui.model.OrderModel
import com.savent.restaurant.ui.model.dish.DishModel
import com.savent.restaurant.ui.model.menu_category.MenuCategoryModel
import com.savent.restaurant.ui.model.payment_method.PaymentMethodModel
import com.savent.restaurant.ui.model.table_and_order.TableAndOrderModel

data class OrdersState (
    val orders: List<TableAndOrderModel> = listOf(),
    val tables: List<Table> = listOf(),
    val diners: List<Diner> = listOf(),
    val dishes: List<DishModel> = listOf(),
    val menuCategories: List<MenuCategoryModel> = listOf(),
    val paymentMethods: List<PaymentMethodModel> = listOf(),
    val order: OrderModel = OrderModel(),
    val isLoading: Boolean = false
)