package com.ericgrandt.totaleconomy.model

sealed interface DomainError

// Server
data object DatabaseError : DomainError
data object BalanceNotFoundInDatabase : DomainError

// Client

fun errorToUserMessage(error: DomainError) = when (error) {
    DatabaseError -> "An error occurred. Please contact an administrator."
    BalanceNotFoundInDatabase -> "An error occurred. Please contact an administrator."
}
