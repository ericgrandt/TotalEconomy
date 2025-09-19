package com.ericgrandt.totaleconomy.result

sealed class Result<out V, out E>

data class Ok<out V>(val value: V) : Result<V, Nothing>()

data class Err<out E>(val error: E) : Result<Nothing, E>()

inline fun <V> runOrCatch(block: () -> V): Result<V, Throwable> =
    try {
        Ok(block())
    } catch (t: Throwable) {
        Err(t)
    }