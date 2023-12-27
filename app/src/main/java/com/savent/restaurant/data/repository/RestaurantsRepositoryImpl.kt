package com.savent.restaurant.data.repository

import com.savent.restaurant.data.common.model.Restaurant
import com.savent.restaurant.data.local.datasource.RestaurantsLocalDatasource
import com.savent.restaurant.data.remote.datasource.RestaurantsRemoteDatasource
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

class RestaurantsRepositoryImpl(
    private val localDatasource: RestaurantsLocalDatasource,
    private val remoteDatasource: RestaurantsRemoteDatasource
) : RestaurantsRepository {

    override suspend fun upsertRestaurants(restaurants: List<Restaurant>): Resource<Int> =
        localDatasource.upsertRestaurants(restaurants)

    override suspend fun getRestaurant(id: Int, companyId: Int): Resource<Restaurant> =
        localDatasource.getRestaurant(id, companyId)

    override fun getAllRestaurants(
        query: String,
        companyId: Int
    ): Flow<Resource<List<Restaurant>>> =
        localDatasource.getRestaurants(query,companyId)

    override suspend fun fetchRestaurants(companyId: Int): Resource<Int> {
        return when (val response = remoteDatasource.getRestaurants(companyId)) {
            is Resource.Success -> localDatasource.upsertRestaurants(response.data)
            is Resource.Error -> Resource.Error(response.message)
        }
    }
}