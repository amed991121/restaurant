package com.savent.restaurant.data.remote.model

import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName
import com.savent.restaurant.data.common.model.PaymentMethod
import com.savent.restaurant.data.local.model.OrderEntity
import com.savent.restaurant.utils.DateTimeObj

data class Order(
    val id: Int,
    @SerializedName("diner_id")
    val dinerId: Int,
    @SerializedName("table_id")
    val tableId: Int,
    val dishes: HashMap<Int, Int>,
    val subtotal: Float,
    val discounts: Float,
    val total: Float,
    val collected: Float,
    val tip: Float,
    val tag: String,
    @SerializedName("payment_method")
    val paymentMethod: PaymentMethod = PaymentMethod.Cash,
    @SerializedName("date_timestamp")
    val date_timestamp: DateTimeObj,
)

fun OrderEntity.toNetwork() = Order(
    remoteId,
    dinerId,
    tableId,
    dishes,
    subtotal,
    discounts,
    total,
    collected,
    tip,
    tag,
    paymentMethod,
    date_timestamp
)
