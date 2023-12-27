package com.savent.restaurant.data.local.datasource

import android.util.Log
import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.Dish
import com.savent.restaurant.data.local.database.dao.DishDao
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class DishesLocalDatasourceImpl(private val dishDao: DishDao) : DishesLocalDatasource {
    override suspend fun addDish(dish: Dish): Resource<Int> =
        withContext(Dispatchers.IO) {
            val result = dishDao.insert(dish)
            if (result > 0L)
                return@withContext Resource.Success(result.toInt())
            Resource.Error(
                Message.StringResource(R.string.add_dish_error)
            )
        }

    override suspend fun getDish(id: Int): Resource<Dish> =
        withContext(Dispatchers.IO) {
            val result = dishDao.get(id)
            if (result != null) return@withContext Resource.Success(result)
            Resource.Error(
                Message.StringResource(R.string.dish_not_found)
            )
        }

    override fun getDishes(query: String, category: Dish.Category): Flow<Resource<List<Dish>>> =
        flow {
            when (category) {
                Dish.Category.ALL -> {
                    dishDao.getAll(query).onEach {
                        emit(Resource.Success(it))
                    }.catch {
                        emit(
                            Resource.Error<List<Dish>>(
                                Message.StringResource(R.string.get_dishes_error)
                            )
                        )
                    }
                }
                else -> {
                    dishDao.getAllWithFilter(query, category).onEach {
                        emit(Resource.Success(it))
                    }.catch {
                        emit(
                            Resource.Error<List<Dish>>(
                                Message.StringResource(R.string.get_dishes_error)
                            )
                        )
                    }
                }
            }.collect()

        }

    override suspend fun getDishes(listId: List<Int>): Resource<List<Dish>> =
        withContext(Dispatchers.IO) {
            val result = dishDao.getDishes(listId)
            return@withContext Resource.Success(result)
        }


    override suspend fun upsertDishes(dishes: List<Dish>): Resource<Int> =
        synchronized(this) {
            runBlocking(Dispatchers.IO) {
                dishDao.getAll().forEach { current ->
                    if (dishes.find { new -> current.id == new.id } == null)
                        dishDao.delete(current)
                }
                val result = dishDao.upsertAll(dishes)
                if (result.isEmpty() && dishes.isNotEmpty())
                    Resource.Error<Int>(
                        Message.StringResource(R.string.update_dishes_error)
                    )
                Resource.Success(result.size)
            }
        }
}