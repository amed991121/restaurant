package com.savent.restaurant.data.local.datasource

import com.savent.restaurant.data.common.model.Company
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

interface CompaniesLocalDatasource {

    suspend fun getCompany(id: Int): Resource<Company>

    fun getCompanies(query: String): Flow<Resource<List<Company>>>

    suspend fun upsertCompanies(companies: List<Company>): Resource<Int>
}