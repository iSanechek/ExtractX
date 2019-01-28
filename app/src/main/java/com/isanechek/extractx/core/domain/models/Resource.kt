package com.isanechek.extractx.core.domain.models

sealed class Resource<out T : Any> {
    class Success<out T: Any>(val data: T) : Resource<T>()
    object Loading : Resource<Nothing>()
    class Error<out T: Any>(val errorMessage: String) : Resource<T>()
}