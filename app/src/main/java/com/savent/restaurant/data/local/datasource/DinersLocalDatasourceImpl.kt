package com.savent.restaurant.data.local.datasource

import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Diner
import com.savent.restaurant.data.local.database.dao.DinerDao
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class DinersLocalDatasourceImpl(private val dinerDao: DinerDao) :
    DinersLocalDatasource {

    override suspend fun addDiner(diner: Diner): Resource<Int> =
        withContext(Dispatchers.IO) {
            val result = dinerDao.insert(diner)
            if (result > 0L)
                return@withContext Resource.Success(result.toInt())
            Resource.Error(
                Message.StringResource(R.string.add_diner_error)
            )
        }

    override suspend fun getDiner(id: Int): Resource<Diner> =
        withContext(Dispatchers.IO) {
            val result = dinerDao.get(id)
            if (result != null)
                return@withContext Resource.Success(result)
            Resource.Error(
                Message.StringResource(R.string.diner_not_found)
            )
        }

    override fun getDiners(query: String): Flow<Resource<List<Diner>>> = flow {
        dinerDao.getAll(query).onEach {
            emit(Resource.Success(it))
        }.catch {
            Resource.Error<List<Diner>>(
                Message.StringResource(R.string.get_diners_error)
            )
        }.collect()
    }

    override suspend fun upsertDiners(diners: List<Diner>): Resource<Int> =
        synchronized(this) {
            runBlocking(Dispatchers.IO) {
                dinerDao.getAll().forEach { current ->
                    if (diners.find { new -> current.id == new.id } == null)
                        dinerDao.delete(current)
                }
                val result = dinerDao.upsertAll(diners)
                if (result.isEmpty() && diners.isNotEmpty())
                    return@runBlocking Resource.Error<Int>(
                        Message.StringResource(R.string.update_diners_error)
                    )
                Resource.Success(result.size)
            }
        }
}