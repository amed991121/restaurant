package com.savent.restaurant.data.common.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diners")
data class Diner(
    @PrimaryKey
    val id: Int,
    val name: String,
    @ColumnInfo(name = "paternal")
    val paternalName: String?,
    @ColumnInfo(name = "maternal")
    val maternalName: String?,
    val rfc: String?,
    @ColumnInfo(name = "phone_number")
    val phoneNumber: Long?,
    val email: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: String?

)