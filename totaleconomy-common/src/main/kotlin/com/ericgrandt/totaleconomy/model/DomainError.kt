package com.ericgrandt.totaleconomy.model

sealed interface DomainError

// Server
data object DatabaseError : DomainError
data object BalanceNotFoundInDatabase : DomainError

// Client
data object InsufficientBalance : DomainError

fun errorToUserMessage(error: DomainError) = when (error) {
    DatabaseError -> "An error occurred. Please contact an administrator."
    BalanceNotFoundInDatabase -> "An error occurred. Please contact an administrator."
    InsufficientBalance -> "Insufficient balance"
}
