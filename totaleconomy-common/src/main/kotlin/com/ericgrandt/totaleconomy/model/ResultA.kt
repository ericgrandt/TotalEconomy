package com.ericgrandt.totaleconomy.model

sealed class ResultA<out T> {
    data class Success<T>(val data: T) : ResultA<T>()
    data class Info<T>(val message: String) : ResultA<T>()
    data class Error<T>(val message: String, val cause: Throwable?) : ResultA<T>()
}