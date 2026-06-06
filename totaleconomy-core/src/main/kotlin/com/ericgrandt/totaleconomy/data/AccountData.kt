package com.ericgrandt.totaleconomy.data

import com.ericgrandt.totaleconomy.data.table.AccountTable
import com.ericgrandt.totaleconomy.mapper.toTEAccount
import com.ericgrandt.totaleconomy.model.TEAccount
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.math.BigDecimal
import java.util.UUID

class AccountData {
    fun createAccount(
        playerId: UUID,
        currencyCode: String,
        defaultBalance: BigDecimal,
    ): Result<TEAccount, Throwable> {
        return runCatching {
            val insertedRow =
                AccountTable
                    .insert {
                        it[AccountTable.playerId] = playerId.toString()
                        it[AccountTable.currencyCode] = currencyCode
                        it[AccountTable.balance] = defaultBalance.toDouble()
                    }

            AccountTable
                .selectAll()
                .where { AccountTable.id eq insertedRow[AccountTable.id] }
                .single()
                .toTEAccount()
        }
    }
}
