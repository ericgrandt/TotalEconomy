package com.ericgrandt.totaleconomy.data.entity

import com.ericgrandt.totaleconomy.data.table.BankTable
import org.jetbrains.exposed.v1.core.ResultRow
import java.util.UUID

data class Bank(
    val id: UUID,
    val accountId: UUID,
    val balance: Double,
) {
    companion object {
        fun fromRow(row: ResultRow) =
            Bank(
                id = UUID.fromString(row[BankTable.id]),
                accountId = UUID.fromString(row[BankTable.accountId]),
                balance = row[BankTable.balance],
            )
    }
}
