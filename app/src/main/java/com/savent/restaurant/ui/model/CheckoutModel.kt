package com.savent.restaurant.ui.model

data class CheckoutModel(
    val subtotal: String = "00,00",
    val taxes: String = "00,00",
    val discounts: String = "",
    val total: String = "00,00",
    val collected: String = "",
    val change: String = "00,00"
)