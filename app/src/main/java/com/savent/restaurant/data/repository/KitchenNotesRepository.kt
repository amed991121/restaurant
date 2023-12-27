package com.savent.restaurant.data.repository

import com.savent.restaurant.data.local.model.KitchenNote
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow

interface KitchenNotesRepository {

    suspend fun upsertNote(kitchenNote: KitchenNote): Resource<Int>

    fun getNote(orderId: Int): Flow<KitchenNote?>

    suspend fun deleteNote(orderId: Int): Resource<Int>
}