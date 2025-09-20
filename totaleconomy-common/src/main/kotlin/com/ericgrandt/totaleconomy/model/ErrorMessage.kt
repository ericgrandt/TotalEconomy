package com.ericgrandt.totaleconomy.model

sealed interface ErrorMessage

// Internal
data object DatabaseError : ErrorMessage

fun errorToUserMessage(error: ErrorMessage) = when (error) {
    DatabaseError -> "An error occurred. Please contact and administrator."
}