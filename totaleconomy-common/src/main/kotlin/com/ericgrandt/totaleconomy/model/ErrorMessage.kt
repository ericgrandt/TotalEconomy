package com.ericgrandt.totaleconomy.model

sealed interface ErrorMessage

// Server
data object DatabaseErrorN : ErrorMessage

// Client

fun errorToUserMessage(error: ErrorMessage) = when (error) {
    DatabaseErrorN -> "An error occurred. Please contact an administrator."
}