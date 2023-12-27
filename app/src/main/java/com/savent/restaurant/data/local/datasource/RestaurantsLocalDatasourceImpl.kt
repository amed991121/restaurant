package com.savent.restaurant.data.local.datasource

import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Company
import com.savent.restaurant.data.common.model.Restaurant
import com.savent.restaurant.data.local.database.dao.RestaurantDao
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class RestaurantsLocalDatasourceImpl(private val restaurantDao: RestaurantDao) :
    RestaurantsLocalDatasource {
    override suspend fun getRestaurant(id: Int, companyId: Int): Resource<Restaurant> =
        withContext(Dispatchers.IO) {
            val result = restaurantDao.get(id, companyId)
            if (result != null) return@withContext Resource.Success(result)
            Resource.Error(
                Message.StringResource(R.string.restaurant_not_found)
            )
        }

    override fun getRestaurants(query: String, companyId: Int): Flow<Resource<List<Restaurant>>> =
        flow {
            restaurantDao.getAll(query, companyId).onEach {
                emit(Resource.Success(it))
            }.catch {
                Resource.Error<List<Company>>(
                    Message.StringResource(R.string.get_restaurants_error)
                )
            }.collect()
        }

    override suspend fun upsertRestaurants(restaurants: List<Restaurant>): Resource<Int> =
        synchronized(this) {
            runBlocking(Dispatchers.IO) {
                restaurantDao.getAll().forEach { current ->
                    if (restaurants.find { new ->
                            "${current.id}${current.companyId}" == "${new.id}${new.companyId}"
                        } == null
                    )
                        restaurantDao.delete(current)
                }
                val result = restaurantDao.upsertAll(restaurants)
                if (result.isEmpty() && restaurants.isNotEmpty())
                    return@runBlocking Resource.Error<Int>(
                        Message.StringResource(R.string.update_restaurants_error)
                    )
                Resource.Success(result.size)
            }
        }
}