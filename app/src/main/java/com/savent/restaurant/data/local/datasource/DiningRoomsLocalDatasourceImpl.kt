package com.savent.restaurant.data.local.datasource

import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.DiningRoom
import com.savent.restaurant.data.local.database.dao.DiningRoomDao
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class DiningRoomsLocalDatasourceImpl(private val diningRoomDao: DiningRoomDao) :
    DiningRoomsLocalDatasource {

    override suspend fun addDiningRoom(diningRoom: DiningRoom): Resource<Int> =
        withContext(Dispatchers.IO) {
            val result = diningRoomDao.insert(diningRoom)
            if (result > 0L)
                Resource.Success(result)
            Resource.Error(
                Message.StringResource(R.string.add_diningroom_error))
        }

    override suspend fun getDiningRoom(id: Int): Resource<DiningRoom> =
        withContext(Dispatchers.IO) {
            val result = diningRoomDao.get(id)
            if (result != null)
                Resource.Success(result)
            Resource.Error(Message.StringResource(R.string.diningroon_not_found))
        }

    override fun getDiningRooms(query: String): Flow<Resource<List<DiningRoom>>> = flow {
        diningRoomDao.getAll(query).onEach {
            emit(Resource.Success(it))
        }.catch {
            Resource.Error<List<DiningRoom>>(Message.StringResource(R.string.get_diningrooms_error))
        }
    }

    override suspend fun upsertDiningRooms(diningRooms: List<DiningRoom>): Resource<Int> =
        synchronized(this) {
            runBlocking(Dispatchers.IO) {
                diningRoomDao.getAll().forEach { current ->
                    if (diningRooms.find { new -> current.id == new.id } == null)
                        diningRoomDao.delete(current)
                }
                val result = diningRoomDao.upsertAll(diningRooms)
                if (result.isEmpty())
                    Resource.Error<Int>(
                        Message.StringResource(R.string.update_diningrooms_error)
                    )
                Resource.Success(result.size)
            }
        }

}