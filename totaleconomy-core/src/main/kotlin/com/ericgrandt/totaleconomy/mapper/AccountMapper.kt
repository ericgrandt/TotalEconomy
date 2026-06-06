package com.ericgrandt.totaleconomy.mapper

import com.ericgrandt.totaleconomy.data.table.AccountTable
import com.ericgrandt.totaleconomy.model.TEAccount
import org.jetbrains.exposed.v1.core.ResultRow
import java.math.BigDecimal
import java.util.UUID

fun ResultRow.toTEAccount(): TEAccount {
    return TEAccount(
        playerId = UUID.fromString(this[AccountTable.playerId]),
        currencyCode = this[AccountTable.currencyCode],
        balance = BigDecimal.valueOf(this[AccountTable.balance]),
    )
}
