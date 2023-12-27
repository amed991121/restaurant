package com.savent.restaurant.data.common.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tables")
data class Table(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "dining_room_id")
    val diningRoomId: Int,
    val name: String
)