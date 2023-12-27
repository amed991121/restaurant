package com.savent.restaurant.data.local.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "printers",
    indices = [
        Index(
            name = "location",
            value = ["location"]
        )
    ]
)
data class Printer(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val address: String,
    val location: Location
) {
    enum class Location {
        Kitchen, DiningRoom
    }
}
