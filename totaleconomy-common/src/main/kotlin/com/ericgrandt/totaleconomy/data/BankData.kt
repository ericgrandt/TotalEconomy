package com.ericgrandt.totaleconomy.data

import com.ericgrandt.totaleconomy.data.table.BankTable
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import org.jetbrains.exposed.v1.jdbc.insertIgnore
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
}
