package com.ericgrandt.totaleconomy.model

import java.math.BigDecimal
import java.util.UUID

data class TEAccount(
    override val playerId: UUID,
    override val currencyCode: String,
    override val balance: BigDecimal,
) : Account
