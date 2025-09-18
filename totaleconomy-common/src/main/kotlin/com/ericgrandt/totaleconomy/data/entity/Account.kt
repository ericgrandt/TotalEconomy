package com.ericgrandt.totaleconomy.data.entity

import java.time.Instant
import java.util.UUID

data class Account(val id: UUID, val createdAt: Instant)