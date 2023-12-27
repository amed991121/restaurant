package com.savent.restaurant.domain.usecase

import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Dish
import com.savent.restaurant.ui.model.menu_category.MenuCategoryModel
import com.savent.restaurant.utils.MenuCategoryNames

class GetMenuCategoriesWithImageUseCase() {

    operator fun invoke(): List<MenuCategoryModel> {
        val categoryList = Dish.Category.values()
        val menuCategoryList = mutableListOf<MenuCategoryModel>()
        categoryList.forEach { category ->
            menuCategoryList.add(
                when (category) {
                    Dish.Category.SEAFOOD -> MenuCategoryModel(
                        type = category,
                        categoryName = MenuCategoryNames.SEAFOOD,
                        resId = R.drawable.crab
                    )
                    Dish.Category.FISH -> MenuCategoryModel(
                        type = category,
                        categoryName = MenuCategoryNames.FISH,
                        resId = R.drawable.fish
                    )
                    Dish.Category.FRIED -> MenuCategoryModel(
                        type = category,
                        categoryName = MenuCategoryNames.FRIED,
                        resId = R.drawable.fried1
                    )
                    Dish.Category.WATER -> MenuCategoryModel(
                        type = category,
                        categoryName = MenuCategoryNames.WATER,
                        resId = R.drawable.water
                    )
                    Dish.Category.SOFT_DRINK -> MenuCategoryModel(
                        type = category,
                        categoryName = MenuCategoryNames.SOFT_DRINK,
                        resId = R.drawable.soda
                    )
                    Dish.Category.COFFEE -> MenuCategoryModel(
                        type = category,
                        categoryName = MenuCategoryNames.COFFEE,
                        resId = R.drawable.coffee
                    )
                    Dish.Category.BEER -> MenuCategoryModel(
                        type = category,
                        categoryName = MenuCategoryNames.BEER,
                        resId = R.drawable.beer1
                    )
                    Dish.Category.COCKTAIL -> MenuCategoryModel(
                        type = category,
                        categoryName = MenuCategoryNames.COCKTAIL,
                        resId = R.drawable.salad1
                    )
                    Dish.Category.SOUP -> MenuCategoryModel(
                        type = category,
                        categoryName = MenuCategoryNames.SOUP,
                        resId = R.drawable.soup
                    )

                    Dish.Category.ALL -> MenuCategoryModel(
                        type = category,
                        categoryName = MenuCategoryNames.ALL,
                        resId = R.drawable.ic_dish
                    )

                }
            )
        }
        menuCategoryList.removeIf { it.type == Dish.Category.ALL }
        return menuCategoryList
    }
}