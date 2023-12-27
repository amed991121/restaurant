package com.savent.restaurant.data.common.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "debts")
data class Debt(
    @PrimaryKey
    val id: Int,
    @ColumnInfo("diner_id")
    @SerializedName("diner_id")
    val dinerId: Int,
    val amount: Float,
    val timestamp: String
)
