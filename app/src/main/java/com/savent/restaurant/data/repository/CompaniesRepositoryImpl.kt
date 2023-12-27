package com.savent.restaurant.data.repository

import com.savent.restaurant.data.common.model.Company
import com.savent.restaurant.data.local.datasource.CompaniesLocalDatasource
import com.savent.restaurant.data.remote.datasource.CompaniesRemoteDatasource
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

class CompaniesRepositoryImpl(
    private val localDatasource: CompaniesLocalDatasource,
    private val remoteDatasource: CompaniesRemoteDatasource
) : CompaniesRepository {

    override suspend fun upsertCompanies(companies: List<Company>): Resource<Int> =
        localDatasource.upsertCompanies(companies)

    override suspend fun getCompany(id: Int): Resource<Company> =
        localDatasource.getCompany(id)

    override fun getAllCompanies(query: String): Flow<Resource<List<Company>>> =
        localDatasource.getCompanies(query)

    override suspend fun fetchCompanies(): Resource<Int> {
        return when (val response = remoteDatasource.getCompanies()) {
            is Resource.Success -> localDatasource.upsertCompanies(response.data)
            is Resource.Error -> Resource.Error(response.message)
        }
    }
}