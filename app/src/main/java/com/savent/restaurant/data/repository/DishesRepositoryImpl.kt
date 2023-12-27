package com.savent.restaurant.data.repository

import android.util.Log
import com.google.gson.Gson
import com.savent.restaurant.data.common.model.Dish
import com.savent.restaurant.data.local.datasource.DishesLocalDatasource
import com.savent.restaurant.data.remote.datasource.DishesRemoteDatasource
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

class DishesRepositoryImpl(
    private val localDatasource: DishesLocalDatasource,
    private val remoteDatasource: DishesRemoteDatasource,
) : DishesRepository {

    override suspend fun upsertDishes(dishes: List<Dish>): Resource<Int> =
        localDatasource.upsertDishes(dishes)

    override suspend fun addDish(restaurantId: Int, dish: Dish): Resource<Int> {
        val response = remoteDatasource.insertDish(restaurantId, dish)
        if (response is Resource.Error)
            return response
        return localDatasource.addDish(dish)
    }

    override suspend fun getDish(id: Int): Resource<Dish> =
        localDatasource.getDish(id)

    override fun getAllDishes(query: String, category: Dish.Category): Flow<Resource<List<Dish>>> =
        localDatasource.getDishes(query,category)

    override suspend fun getDishes(listId: List<Int>): Resource<List<Dish>> =
        localDatasource.getDishes(listId)

    override suspend fun fetchDishes(restaurantId: Int, companyId: Int): Resource<Int> {
        return when (val response = remoteDatasource.getDishes(restaurantId, companyId)) {
            is Resource.Success -> {
                Log.d("log_", "dishes${Gson().toJson(response.data)}")
                localDatasource.upsertDishes(response.data)
            }
            is Resource.Error -> Resource.Error(response.message)
        }
    }

    override suspend fun updateDish(restaurantId: Int, dish: Dish): Resource<Int> =
        remoteDatasource.updateDish(restaurantId, dish)

    override suspend fun deleteDish(restaurantId: Int, id: Int): Resource<Int> =
        remoteDatasource.deleteDish(restaurantId, id)
}