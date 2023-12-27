package com.savent.restaurant.data.remote.datasource

import com.savent.restaurant.data.common.model.Company
import com.savent.restaurant.utils.Resource

interface CompaniesRemoteDatasource {
    suspend fun getCompanies(): Resource<List<Company>>
}