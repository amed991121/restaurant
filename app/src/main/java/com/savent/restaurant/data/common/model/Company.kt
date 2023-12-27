package com.savent.restaurant.data.common.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("companies")
data class Company(
    @PrimaryKey
    val id: Int,
    val name: String,
)