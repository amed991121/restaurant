package com.savent.restaurant.data.local.datasource

import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Debt
import com.savent.restaurant.data.local.database.dao.DebtDao
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class DebtsLocalDatasourceImpl(private val debtDao: DebtDao) :
    DebtsLocalDatasource {

    override suspend fun getDebt(id: Int): Resource<Debt> =
        withContext(Dispatchers.IO) {
            val result = debtDao.get(id)
            if (result != null)
                Resource.Success(result)
            Resource.Error(
                Message.StringResource(R.string.debt_not_found)
            )
        }

    override fun getDebts(): Flow<Resource<List<Debt>>> = flow {
        debtDao.getAllDebts().onEach {
            emit(Resource.Success(it))
        }.catch {
            Resource.Error<List<Debt>>(
                Message.StringResource(R.string.get_debts_error)
            )
        }.collect()
    }

    override suspend fun upsertDebts(debts: List<Debt>): Resource<Int> =
        synchronized(this) {
            runBlocking(Dispatchers.IO) {
                debtDao.getAll().forEach { current ->
                    if (debts.find { new -> current.id == new.id } == null)
                        debtDao.delete(current)
                }
                val result = debtDao.upsertAll(debts)
                if (result.isEmpty())
                    Resource.Error<Int>(
                        Message.StringResource(R.string.update_debts_error)
                    )
                Resource.Success(result.size)
            }
        }
}