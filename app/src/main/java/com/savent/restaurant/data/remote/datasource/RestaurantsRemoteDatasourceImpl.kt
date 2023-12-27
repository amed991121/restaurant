package com.savent.restaurant.data.remote.datasource

import android.util.Log
import com.google.gson.Gson
import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Restaurant
import com.savent.restaurant.data.remote.ErrorBody
import com.savent.restaurant.data.remote.service.RestaurantsApiService
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RestaurantsRemoteDatasourceImpl(private val restaurantsApiService: RestaurantsApiService) :
    RestaurantsRemoteDatasource {
    override suspend fun getRestaurants(companyId: Int): Resource<List<Restaurant>> =
        withContext(Dispatchers.IO) {
            try {
                val response = restaurantsApiService.getRestaurants(companyId)
                if (response.isSuccessful)
                    return@withContext Resource.Success(response.body()!!)
                Resource.Error(
                    Message.DynamicString(
                        Gson().fromJson(
                            response.errorBody()?.charStream(),
                            ErrorBody::class.java
                        ).message
                    )
                )
            } catch (e: Exception) {
                Resource.Error(Message.StringResource(R.string.fetch_restaurants_error))
            }
        }
}