package com.savent.restaurant.ui.model.sale

import com.savent.restaurant.ui.model.CheckoutModel
import com.savent.restaurant.ui.model.payment_method.PaymentMethodModel

data class SaleModel(
    val id: Int,
    val tableName: String,
    val totalDishes: Int,
    val paymentMethod: PaymentMethodModel,
    val checkout: CheckoutModel,
    val hour: String,
)