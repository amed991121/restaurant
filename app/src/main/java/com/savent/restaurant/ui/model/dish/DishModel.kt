package com.savent.restaurant.ui.model.dish

import com.savent.restaurant.data.common.model.Dish
import com.savent.restaurant.utils.DecimalFormat
import com.savent.restaurant.utils.NameFormat

data class DishModel(
    val id: Int,
    val name: String,
    val price: String,
    val url: String? = null,
    val units: Int = 0
)
fun Dish.toModel() =
    DishModel(
        this.id,
        NameFormat.format(this.name),
        DecimalFormat.format(this.price),
        this.imageUrl
    )
