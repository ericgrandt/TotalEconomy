package com.ericgrandt.totaleconomy.data

import com.ericgrandt.totaleconomy.data.entity.Bank
import com.ericgrandt.totaleconomy.data.table.BankTable
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.select
import java.util.UUID

class BankData {
    fun createBank(accountId: UUID): Result<Int, Throwable> {
        return runCatching {
            BankTable
                .insertIgnore {
                    it[BankTable.id] = UUID.randomUUID().toString()
                    it[BankTable.accountId] = accountId.toString()
                }.insertedCount
        }
    }

    fun getBank(accountId: UUID): Result<Bank?, Throwable> {
        return runCatching {
            BankTable
                .select(BankTable.id, BankTable.accountId, BankTable.balance)
                .where { BankTable.accountId eq accountId.toString() }
                .limit(1)
                .singleOrNull()
                ?.let { Bank.fromRow(it) }
        }
    }
}
