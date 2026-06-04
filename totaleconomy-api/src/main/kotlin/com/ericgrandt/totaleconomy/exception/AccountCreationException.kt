package com.ericgrandt.totaleconomy.exception

class AccountCreationException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
