package com.savent.restaurant.ui.screen.orders

import com.savent.restaurant.data.common.model.Dish
import com.savent.restaurant.data.common.model.PaymentMethod

sealed class OrdersEvent {

    class SearchOrders(val query: String): OrdersEvent()
    class SelectOrder(val orderId: Int): OrdersEvent()
    class SearchTables(val query: String): OrdersEvent()
    class SelectTable(val tableId: Int): OrdersEvent()
    class SearchDiners(val query: String): OrdersEvent()
    class SelectDiner(val dinerId: Int): OrdersEvent()
    class SearchDishes(val query: String, val category: Dish.Category): OrdersEvent()
    class AddOrderTag(val tag: String, val isPersistent: Boolean = false): OrdersEvent()
    class AddDishUnit(val dishId: Int, val isPersistent: Boolean): OrdersEvent()
    class RemoveDishUnit(val dishId: Int, val isPersistent: Boolean): OrdersEvent()
    class UpdateCollectedValues(val discounts: Float, val collected: Float): OrdersEvent()
    class SelectPaymentMethod(val method: PaymentMethod): OrdersEvent()
    object DeleteOrder: OrdersEvent()
    object CreateOrder: OrdersEvent()
    object RegisterOrder: OrdersEvent()
    object ResetOrder: OrdersEvent()
    object SelectPrinter: OrdersEvent()
    class SendKitchenOrderToPrinter(val priorities: HashMap<Int, Int>): OrdersEvent()
    object ScanPrinterDevices: OrdersEvent()
}