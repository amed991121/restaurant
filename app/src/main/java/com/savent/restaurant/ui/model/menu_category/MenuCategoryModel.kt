package com.savent.restaurant.ui.model.menu_category

import androidx.annotation.DrawableRes
import com.savent.restaurant.data.common.model.Dish

data class MenuCategoryModel(
    val type: Dish.Category,
    val categoryName: String,
    @DrawableRes val resId: Int,
    val isSelected: Boolean = false
)