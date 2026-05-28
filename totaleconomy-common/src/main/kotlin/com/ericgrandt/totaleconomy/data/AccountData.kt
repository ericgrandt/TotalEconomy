package com.ericgrandt.totaleconomy.data

import com.ericgrandt.totaleconomy.data.entity.Account
import com.ericgrandt.totaleconomy.data.table.AccountTable
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.select
import java.util.UUID

class AccountData {
    fun createAccount(accountId: UUID): Result<Int, Throwable> =
        runCatching {
            AccountTable
                .insertIgnore {
                    it[id] = accountId.toString()
                }.insertedCount
        }

    fun getAccount(accountId: UUID): Result<Account?, Throwable> =
        runCatching {
            AccountTable
                .select(AccountTable.id, AccountTable.createdAt)
                .where { AccountTable.id eq accountId.toString() }
                .limit(1)
                .singleOrNull()
                ?.let { Account.fromRow(it) }
        }
}
