package com.ericgrandt.totaleconomy.model

sealed interface DomainError

// Server
data object DatabaseError : DomainError

// Client

fun errorToUserMessage(error: DomainError) = when (error) {
    DatabaseError -> "An error occurred. Please contact an administrator."
}
