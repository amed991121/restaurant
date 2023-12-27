package com.savent.restaurant.data.repository

import com.savent.restaurant.R
import com.savent.restaurant.data.local.database.dao.KitchenNoteDao
import com.savent.restaurant.data.local.model.KitchenNote
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class KitchenNotesRepositoryImpl(private val kitchenNoteDao: KitchenNoteDao) :
    KitchenNotesRepository {

    override suspend fun upsertNote(kitchenNote: KitchenNote): Resource<Int> =
        withContext(Dispatchers.IO) {
            val result = kitchenNoteDao.upsertNote(kitchenNote)
            if (result > 0)
                return@withContext Resource.Success(result.toInt())
            Resource.Error(
                Message.StringResource(R.string.save_kitchen_note_error)
            )
        }

    override fun getNote(orderId: Int): Flow<KitchenNote?> =
        kitchenNoteDao.getNote(orderId)


    override suspend fun deleteNote(orderId: Int): Resource<Int> =
        withContext(Dispatchers.IO) {
            val result = kitchenNoteDao.deleteNote(orderId)
            if (result > 0) return@withContext Resource.Success(result)
            Resource.Error(
                Message.StringResource(R.string.delete_kitchen_note_error)
            )
        }
}