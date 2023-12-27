package com.savent.restaurant.ui.screen.food

import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Dish
import com.savent.restaurant.ui.model.dish.DishModel
import com.savent.restaurant.ui.model.menu_category.MenuCategoryModel
import com.savent.restaurant.ui.model.table_and_order.TableAndOrderModel
import com.savent.restaurant.utils.MenuCategoryNames

data class FoodState(
    val employeeName: String = "",
    val dishes: List<DishModel> = listOf(),
    val categoriesListWithImage: List<MenuCategoryModel> = listOf(
        MenuCategoryModel(
            Dish.Category.ALL, MenuCategoryNames.ALL,
            R.drawable.ic_all_fill
        )
    ),
    val categoriesListWithIcon: List<MenuCategoryModel> = listOf(),
    val tableAndOrders: List<TableAndOrderModel> = listOf(),
    val isLoading: Boolean = false,
)