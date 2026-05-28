package com.ericgrandt.totaleconomy.data.entity

import com.ericgrandt.totaleconomy.data.table.BalanceTable
import org.jetbrains.exposed.v1.core.ResultRow
import java.util.UUID

data class Balance(
    val id: UUID,
    val accountId: UUID,
    val balance: Double,
) {
    companion object {
        fun fromRow(row: ResultRow) =
            Balance(
                id = UUID.fromString(row[BalanceTable.id]),
                accountId = UUID.fromString(row[BalanceTable.accountId]),
                balance = row[BalanceTable.balance],
            )
    }
}
