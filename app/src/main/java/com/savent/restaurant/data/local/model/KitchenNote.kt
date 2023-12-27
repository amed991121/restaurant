package com.savent.restaurant.data.local.model

import androidx.room.*

@Entity(
    tableName = "kitchen_notes",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("order_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class KitchenNote(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("order_id")
    val orderId: Int,
    @ColumnInfo("dishes_sent")
    val dishesSent: HashMap<Int,Int>
)