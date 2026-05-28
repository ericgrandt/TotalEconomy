package com.ericgrandt.totaleconomy.data.entity

import com.ericgrandt.totaleconomy.data.table.AccountTable
import org.jetbrains.exposed.v1.core.ResultRow
import java.util.UUID
import kotlin.time.Instant

data class Account(
    val id: UUID,
    val createdAt: Instant,
) {
    companion object {
        fun fromRow(row: ResultRow) =
            Account(
                id = UUID.fromString(row[AccountTable.id]),
                createdAt = row[AccountTable.createdAt],
            )
    }
}
