package com.ericgrandt.totaleconomy.exception

sealed class AccountException(
    cause: Throwable? = null,
) : Exception(cause)

class AccountNotFoundException(
    override val cause: Throwable? = null,
) : AccountException(cause)
