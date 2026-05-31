package com.ericgrandt.totaleconomy.data

import com.ericgrandt.totaleconomy.data.entity.Balance
import com.ericgrandt.totaleconomy.data.table.BalanceTable
import com.ericgrandt.totaleconomy.model.DepositIntoBalance
import com.ericgrandt.totaleconomy.model.SetBalance
import com.ericgrandt.totaleconomy.model.WithdrawFromBalance
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.minus
import org.jetbrains.exposed.v1.core.plus
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.update
import java.util.UUID

class BalanceData {
    // TODO: Take in an optional default balance
    fun createBalance(accountId: UUID): Result<Int, Throwable> {
        return runCatching {
            BalanceTable
                .insertIgnore {
                    it[BalanceTable.id] = UUID.randomUUID().toString()
                    it[BalanceTable.accountId] = accountId.toString()
                }.insertedCount
        }
    }

    fun getBalance(accountId: UUID): Result<Balance?, Throwable> {
        return runCatching {
            BalanceTable
                .select(BalanceTable.id, BalanceTable.accountId, BalanceTable.balance)
                .where { BalanceTable.accountId eq accountId.toString() }
                .limit(1)
                .singleOrNull()
                ?.let { Balance.fromRow(it) }
        }
    }

    fun setBalance(input: SetBalance): Result<Int, Throwable> {
        return runCatching {
            BalanceTable.update({ BalanceTable.accountId eq input.accountId.toString() }) {
                it[BalanceTable.balance] = input.balance
            }
        }
    }

    fun withdrawFromBalance(input: WithdrawFromBalance): Result<Int, Throwable> {
        return runCatching {
            BalanceTable.update({ BalanceTable.accountId eq input.accountId.toString() }) {
                it[BalanceTable.balance] = BalanceTable.balance - input.amount
            }
        }
    }

    fun depositIntoBalance(input: DepositIntoBalance): Result<Int, Throwable> {
        return runCatching {
            BalanceTable.update({ BalanceTable.accountId eq input.accountId.toString() }) {
                it[BalanceTable.balance] = BalanceTable.balance + input.amount
            }
        }
    }
}
