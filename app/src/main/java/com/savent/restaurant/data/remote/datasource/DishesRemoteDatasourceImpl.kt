package com.savent.restaurant.data.remote.datasource

import com.google.gson.Gson
import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Dish
import com.savent.restaurant.data.remote.ErrorBody
import com.savent.restaurant.data.remote.service.DishesApiService
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DishesRemoteDatasourceImpl(private val dishesApiService: DishesApiService) :
    DishesRemoteDatasource {

    override suspend fun getDishes(restaurantId: Int, companyId: Int): Resource<List<Dish>> =
        withContext(Dispatchers.IO) {
            try {
                val response = dishesApiService.getDishes(restaurantId, companyId)
                if (response.isSuccessful)
                    return@withContext Resource.Success(response.body()!!)
                Resource.Error(Message.DynamicString(
                    Gson().fromJson(
                        response.errorBody()?.charStream(),
                        ErrorBody::class.java
                    ).message
                ))
            } catch (e: Exception) {
                Resource.Error(Message.StringResource(R.string.fetch_dishes_error))
            }
        }


    override suspend fun insertDish(restaurantId: Int, dish: Dish): Resource<Int> =
        withContext(Dispatchers.IO) {
            try {
                val response = dishesApiService.insertDish(restaurantId, dish)
                if (response.isSuccessful)
                    return@withContext Resource.Success(response.body()!!)
                Resource.Error(Message.DynamicString(response.errorBody().toString()))
            } catch (e: Exception) {
                Resource.Error(Message.StringResource(R.string.add_dish_error))
            }
        }


    override suspend fun updateDish(restaurantId: Int, dish: Dish): Resource<Int> =
        withContext(Dispatchers.IO) {
            try {
                val response = dishesApiService.updateDish(restaurantId, dish)
                if (response.isSuccessful)
                    return@withContext Resource.Success(response.body()!!)
                Resource.Error(Message.DynamicString(response.errorBody().toString()))
            } catch (e: Exception) {
                Resource.Error(Message.StringResource(R.string.update_dish_error))
            }
        }

    override suspend fun deleteDish(restaurantId: Int, dishId: Int): Resource<Int> =
        withContext(Dispatchers.IO) {
            try {
                val response = dishesApiService.deleteDish(restaurantId, dishId)
                if (response.isSuccessful)
                    return@withContext Resource.Success(response.body()!!)
                Resource.Error(Message.DynamicString(response.errorBody().toString()))
            } catch (e: Exception) {
                Resource.Error(Message.StringResource(R.string.delete_dish_error))
            }
        }
}