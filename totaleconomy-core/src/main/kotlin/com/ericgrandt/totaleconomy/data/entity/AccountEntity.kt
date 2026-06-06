package com.ericgrandt.totaleconomy.data.entity

import java.util.UUID
import kotlin.time.Instant

data class AccountEntity(
    val id: Int,
    val playerId: UUID,
    val currencyCode: String,
    val balance: Double,
    val createdAt: Instant,
)
