package com.savent.restaurant.data.remote.service

import com.savent.restaurant.AppConstants
import com.savent.restaurant.data.common.model.Company
import retrofit2.Response
import retrofit2.http.GET

interface CompaniesApiService {
    @GET(AppConstants.COMPANIES_API_PATH)
    suspend fun getCompanies(): Response<List<Company>>
}