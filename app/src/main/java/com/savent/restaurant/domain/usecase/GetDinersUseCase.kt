package com.savent.restaurant.domain.usecase

import com.savent.restaurant.data.repository.DinersRepository
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class GetDinersUseCase(private val dinersRepository: DinersRepository) {

    /*operator fun invoke(query: String): Flow<Resource<List<DinerModel>>> = flow {
        dinersRepository.getAllDiners(query).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    emit(Resource.Error(result.message))
                    return@onEach
                }
                is Resource.Success -> {
                    emit(Resource.Success(
                        (result.data)
                            .map { it.toModel() }))
                }
            }
        }.collect()
    }*/
}