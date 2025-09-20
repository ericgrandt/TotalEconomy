package com.ericgrandt.totaleconomy.data.entity

import java.util.UUID

data class Balance(val id: UUID, val accountId: UUID, val balance: Double)
