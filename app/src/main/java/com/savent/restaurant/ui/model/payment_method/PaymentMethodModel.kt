package com.savent.restaurant.ui.model.payment_method

import androidx.annotation.DrawableRes
import com.savent.restaurant.data.common.model.PaymentMethod

data class PaymentMethodModel(
    @DrawableRes val resId: Int,
    val name: String,
    val method: PaymentMethod
)