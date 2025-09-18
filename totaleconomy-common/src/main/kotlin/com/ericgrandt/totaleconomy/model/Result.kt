package com.ericgrandt.totaleconomy.model

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Info<T>(val message: String) : Result<T>()
    data class Error<T>(val message: String, val cause: Throwable?) : Result<T>()
}