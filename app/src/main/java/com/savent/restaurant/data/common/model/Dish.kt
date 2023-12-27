package com.savent.restaurant.data.common.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "dishes")
data class Dish(
    @PrimaryKey
    val id: Int,
    val name: String,
    @ColumnInfo(name = "image_url")
    @SerializedName("image_url")
    val imageUrl: String?,
    val price: Float,
    val category: Category,
    val units: Int = 0
) {

    enum class Category {
        ALL, SEAFOOD, FISH, FRIED, WATER, SOFT_DRINK, COFFEE, BEER, COCKTAIL, SOUP
    }

}