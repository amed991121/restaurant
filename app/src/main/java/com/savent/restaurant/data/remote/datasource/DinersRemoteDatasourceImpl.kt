package com.savent.restaurant.data.remote.datasource

import android.util.Log
import com.google.gson.Gson
import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Diner
import com.savent.restaurant.data.remote.ErrorBody
import com.savent.restaurant.data.remote.service.DinersApiService
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DinersRemoteDatasourceImpl(private val dinersApiService: DinersApiService) :
    DinersRemoteDatasource {

    override suspend fun getDiners(restaurantId: Int, companyId: Int): Resource<List<Diner>> =
        withContext(Dispatchers.IO) {
            try {
                val response = dinersApiService.getDiners(restaurantId, companyId)
                if (response.isSuccessful)
                    return@withContext Resource.Success(response.body()!!)
                Resource.Error(Message.DynamicString(
                    Gson().fromJson(
                        response.errorBody()?.charStream(),
                        ErrorBody::class.java
                    ).message
                ))
            } catch (e: Exception) {
                Resource.Error(Message.StringResource(R.string.fetch_diners_error))
            }
        }

    override suspend fun insertDiner(restaurantId: Int, diner: Diner): Resource<Int> =
        withContext(Dispatchers.IO) {
            try {
                val response = dinersApiService.insertDiner(restaurantId, diner)
                if (response.isSuccessful)
                    return@withContext Resource.Success(response.body()!!)
                Resource.Error(Message.DynamicString(response.errorBody().toString()))
            } catch (e: Exception) {
                Resource.Error(Message.StringResource(R.string.add_diner_error))
            }
        }

    override suspend fun updateDiner(restaurantId: Int, diner: Diner): Resource<Int> =
        withContext(Dispatchers.IO) {
            try {
                val response = dinersApiService.updateDiner(restaurantId, diner)
                if (response.isSuccessful)
                    return@withContext Resource.Success(response.body()!!)
                Resource.Error(Message.DynamicString(response.errorBody().toString()))
            } catch (e: Exception) {
                Resource.Error(Message.StringResource(R.string.update_diner_error))
            }
        }

    override suspend fun deleteDiner(restaurantId: Int, dinerId: Int): Resource<Int> =
        withContext(Dispatchers.IO) {
            try {
                val response = dinersApiService.deleteDiner(restaurantId, dinerId)
                if (response.isSuccessful)
                    return@withContext Resource.Success(response.body()!!)
                Resource.Error(Message.DynamicString(response.errorBody().toString()))
            } catch (e: Exception) {
                Resource.Error(Message.StringResource(R.string.delete_diner_error))
            }
        }
}