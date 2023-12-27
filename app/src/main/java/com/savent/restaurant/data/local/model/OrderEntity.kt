package com.savent.restaurant.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.savent.restaurant.data.common.model.PaymentMethod
import com.savent.restaurant.data.remote.model.Order
import com.savent.restaurant.utils.DateTimeObj

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "remote_id")
    val remoteId: Int = Int.MAX_VALUE,
    @ColumnInfo(name = "diner_id")
    val dinerId: Int = 0,
    @ColumnInfo(name = "table_id")
    val tableId: Int = 0,
    val status: Status = Status.OPEN,
    val dishes: HashMap<Int, Int> = HashMap(),
    val subtotal: Float = 0F,
    val discounts: Float = 0F,
    val total: Float = 0F,
    val collected: Float = 0F,
    val tip: Float = 0F,
    val tag: String = "",
    @ColumnInfo(name = "payment_method")
    val paymentMethod: PaymentMethod = PaymentMethod.Cash,
    @ColumnInfo(name = "date_timestamp")
    val date_timestamp: DateTimeObj = DateTimeObj.fromLong(System.currentTimeMillis()),
)

fun Order.toLocal() = OrderEntity(
    id,
    id,
    dinerId,
    tableId,
    Status.CLOSED,
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
