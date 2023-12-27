package com.savent.restaurant.data.remote.datasource

import android.util.Log
import com.google.gson.Gson
import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Company
import com.savent.restaurant.data.remote.ErrorBody
import com.savent.restaurant.data.remote.service.CompaniesApiService
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class
CompaniesRemoteDatasourceImpl(private val companiesApiService: CompaniesApiService) :
    CompaniesRemoteDatasource {
    override suspend fun getCompanies(): Resource<List<Company>> =
        withContext(Dispatchers.IO) {
            try {
                val response = companiesApiService.getCompanies()
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
                Resource.Error(Message.StringResource(R.string.fecth_companies_error))
            }
        }
}