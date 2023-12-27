package com.savent.restaurant.data.local.datasource

import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Company
import com.savent.restaurant.data.local.database.dao.CompanyDao
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class CompaniesLocalDatasourceImpl(private val companyDao: CompanyDao) : CompaniesLocalDatasource {

    override suspend fun getCompany(id: Int): Resource<Company> =
        withContext(Dispatchers.IO) {
            val result = companyDao.get(id)
            if (result != null)  return@withContext Resource.Success(result)
            Resource.Error(
                Message.StringResource(R.string.company_not_found)
            )
        }

    override fun getCompanies(query: String): Flow<Resource<List<Company>>> = flow {
        companyDao.getAll(query).onEach {
            emit(Resource.Success(it))
        }.catch {
            Resource.Error<List<Company>>(
                Message.StringResource(R.string.get_companies_error)
            )
        }.collect()
    }

    override suspend fun upsertCompanies(companies: List<Company>): Resource<Int> =
        synchronized(this) {
            runBlocking(Dispatchers.IO) {
                companyDao.getAll().forEach { current ->
                    if (companies.find { new -> current.id == new.id } == null)
                        companyDao.delete(current)
                }
                val result = companyDao.upsertAll(companies)
                if (result.isEmpty() && companies.isNotEmpty())
                    return@runBlocking Resource.Error<Int>(
                        Message.StringResource(R.string.update_companies_error)
                    )
                Resource.Success(result.size)
            }
        }
}