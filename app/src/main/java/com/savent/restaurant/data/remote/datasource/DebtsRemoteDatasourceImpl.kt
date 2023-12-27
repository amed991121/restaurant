package com.savent.restaurant.data.remote.datasource

import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Debt
import com.savent.restaurant.data.remote.service.DebtsApiService
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource

class DebtsRemoteDatasourceImpl(private val debtsApiService: DebtsApiService) :
    DebtsRemoteDatasource {

    override suspend fun getDebts(restaurantId: Int): Resource<List<Debt>> {
        try {
            val response = debtsApiService.getDebts(restaurantId)
            if (response.isSuccessful)
                return Resource.Success(response.body()!!)
            return Resource.Error(Message.DynamicString(response.errorBody().toString()))
        } catch (e: Exception) {
            return Resource.Error(Message.StringResource(R.string.get_debts_error))
        }
    }

    override suspend fun insertDebt(restaurantId: Int, debt: Debt): Resource<Int> {
        try {
            val response = debtsApiService.insertDebt(restaurantId, debt)
            if (response.isSuccessful)
                return Resource.Success(response.body()!!)
            return Resource.Error(Message.DynamicString(response.errorBody().toString()))
        }catch (e: Exception) {
            return Resource.Error(Message.StringResource(R.string.add_debt_error))
        }
    }

    override suspend fun updateDebt(restaurantId: Int, debt: Debt): Resource<Int> {
        try {
            val response = debtsApiService.updateDebt(restaurantId, debt)
            if (response.isSuccessful)
                return Resource.Success(response.body()!!)
            return Resource.Error(Message.DynamicString(response.errorBody().toString()))
        }catch (e: Exception) {
            return Resource.Error(Message.StringResource(R.string.update_debt_error))
        }
    }

    override suspend fun deleteDebt(restaurantId: Int, debtId: Int): Resource<Int> {
        try {
            val response = debtsApiService.deleteDebt(restaurantId, debtId)
            if (response.isSuccessful)
                return Resource.Success(response.body()!!)
            return Resource.Error(Message.DynamicString(response.errorBody().toString()))
        }catch (e: Exception) {
            return Resource.Error(Message.StringResource(R.string.delete_debt_error))
        }
    }
}