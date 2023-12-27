package com.savent.restaurant.domain.usecase

import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Dish
import com.savent.restaurant.ui.model.menu_category.MenuCategoryModel
import com.savent.restaurant.utils.MenuCategoryNames

class GetMenuCategoriesWithIconUseCase {
    operator fun invoke(typeSelected: Dish.Category): List<MenuCategoryModel> {
        val categoryList = Dish.Category.values()
        val menuCategoryList = mutableListOf<MenuCategoryModel>()
        categoryList.forEach { category ->
            menuCategoryList.add(
                when (category) {
                    Dish.Category.SEAFOOD -> MenuCategoryModel(
                        type = category,
                        categoryName = MenuCategoryNames.SEAFOOD,
                        resId = R.drawable.ic_crab,
                        isSelected = category == typeSelected
                    )
                    Dish.Category.FISH -> MenuCategoryModel(
                        type = category,
                        categoryName = MenuCategoryNames.FISH,
                        resId = R.drawable.ic_fish,
                        isSelected = category == typeSelected
                    )
                    Dish.Category.FRIED -> MenuCategoryModel(
                        type = category,
                        categoryName = MenuCategoryNames.FRIED,
                        resId = R.drawable.ic_salmon,
                        isSelected = category == typeSelected
                    )
                    Dish.Category.WATER -> MenuCategoryModel(
                        type = category,
                        categoryName = MenuCategoryNames.WATER,
                        resId = R.drawable.ic_water,
                        isSelected = category == typeSelected
                    )
                    Dish.Category.SOFT_DRINK -> MenuCategoryModel(
                        type = category,
                        categoryName = MenuCategoryNames.SOFT_DRINK,
                        resId = R.drawable.ic_soda,
                        isSelected = category == typeSelected
                    )
                    Dish.Category.COFFEE -> MenuCategoryModel(
                        type = category,
                        categoryName = MenuCategoryNames.COFFEE,
                        resId = R.drawable.ic_cofee,
                        isSelected = category == typeSelected
                    )
                    Dish.Category.BEER -> MenuCategoryModel(
                        type = category,
                        categoryName = MenuCategoryNames.BEER,
                        resId = R.drawable.ic_beer3,
                        isSelected = category == typeSelected
                    )
                    Dish.Category.COCKTAIL -> MenuCategoryModel(
                        type = category,
                        categoryName = MenuCategoryNames.COCKTAIL,
                        resId = R.drawable.ic_salad,
                        isSelected = category == typeSelected
                    )
                    Dish.Category.SOUP -> MenuCategoryModel(
                        type = category,
                        categoryName = MenuCategoryNames.SOUP,
                        resId = R.drawable.ic_soup1,
                        isSelected = category == typeSelected
                    )
                    Dish.Category.ALL -> MenuCategoryModel(
                        type = category,
                        categoryName = MenuCategoryNames.ALL,
                        resId = R.drawable.ic_all_fill,
                        isSelected = category == typeSelected
                    )

                }
            )
        }
        return menuCategoryList
    }
}