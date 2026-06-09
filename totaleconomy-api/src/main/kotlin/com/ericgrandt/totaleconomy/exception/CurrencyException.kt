package com.ericgrandt.totaleconomy.exception

sealed class CurrencyException(
    cause: Throwable? = null,
) : Exception(cause)

class CurrencyNotFoundException(
    override val cause: Throwable? = null,
) : CurrencyException(cause)
