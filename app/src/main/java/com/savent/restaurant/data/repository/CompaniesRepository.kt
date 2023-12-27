package com.savent.restaurant.data.repository

import com.savent.restaurant.data.common.model.Company
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

interface CompaniesRepository {

    suspend fun upsertCompanies(companies: List<Company>): Resource<Int>

    suspend fun getCompany(id: Int): Resource<Company>

    fun getAllCompanies(query: String): Flow<Resource<List<Company>>>

    suspend fun fetchCompanies(): Resource<Int>
}