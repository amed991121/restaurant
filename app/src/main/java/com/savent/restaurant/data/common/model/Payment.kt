package com.savent.restaurant.data.common.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class Payment(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "debt_id")
    val debtId: Int,
    val debt: Float,
    val charged: Float,
    val leftover: Float,
    val timestamp: String
)
