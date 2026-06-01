package com.ericgrandt.totaleconomy.model

import java.util.UUID

data class CreateBank(
    val accountId: UUID,
    val name: String,
)

data class DeleteBank(
    val name: String,
)
