package com.ericgrandt.totaleconomy.data.entity

import kotlin.time.Instant

data class CurrencyEntity(
    val id: Int,
    val code: String,
    val name: String,
    val pluralName: String,
    val symbol: String?,
    val fractionalDigits: Int,
    val isDefault: Boolean,
    val createdAt: Instant,
)
