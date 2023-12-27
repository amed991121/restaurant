package com.savent.restaurant.ui.model

import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.PaymentMethod
import com.savent.restaurant.data.local.model.OrderEntity
import com.savent.restaurant.ui.model.dish.DishModel
import com.savent.restaurant.ui.model.payment_method.PaymentMethodModel
import com.savent.restaurant.ui.model.table_and_order.OrderNameModel
import com.savent.restaurant.utils.PaymentMethodNames

data class OrderModel(
    val tableName: String = "",
    val tag: String = "",
    val dishes: List<DishModel> = listOf(),
    val paymentMethod: PaymentMethodModel = PaymentMethodModel(
        R.drawable.ic_money,
        PaymentMethodNames.CASH,
        PaymentMethod.Cash
    ),
    val checkout: CheckoutModel = CheckoutModel(),
    val pendingKitchenDishes: List<DishModel> = listOf()
)
