package com.ericgrandt.totaleconomy.result

sealed class Result<out V, out E> {
    // Don't use this unless it's in a test, or you're super confident there's a value
    fun ok(): V = when (this) {
        is Ok -> value
        is Err -> throw IllegalStateException("attempting to call ok() on an Err Result")
    }

    // Don't use this unless it's in a test, or you're super confident there's an error
    fun err(): E = when (this) {
        is Ok -> throw IllegalStateException("attempting to call err() on an Ok Result")
        is Err -> error
    }
}

data class Ok<out V>(val value: V) : Result<V, Nothing>()

data class Err<out E>(val error: E) : Result<Nothing, E>()

inline fun <V> runOrCatch(block: () -> V): Result<V, Throwable> =
    try {
        Ok(block())
    } catch (t: Throwable) {
        Err(t)
    }