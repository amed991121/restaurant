package com.savent.restaurant.data.common.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "restaurants",
    indices = [Index(value = ["id", "company_id"], unique = true)]
)
data class Restaurant(
    @PrimaryKey(autoGenerate = true)
    val primaryKey: Int,
    val id: Int,
    @SerializedName("company_id")
    @ColumnInfo("company_id")
    val companyId: Int,
    val name: String,
)