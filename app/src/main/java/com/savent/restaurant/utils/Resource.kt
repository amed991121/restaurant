package com.savent.restaurant.utils

import org.jetbrains.annotations.NotNull

sealed class Resource<T> {

    class Success<T>(val data: T): Resource<T>()

    class Error<T>(val message: Message): Resource<T>()
}
