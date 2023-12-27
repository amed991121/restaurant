package com.savent.restaurant.data.common.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dining_rooms")
data class DiningRoom(
    @PrimaryKey
    val id: Int,
    val name: String
)