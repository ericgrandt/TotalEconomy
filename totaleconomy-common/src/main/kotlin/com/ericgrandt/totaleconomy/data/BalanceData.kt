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
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.update
import java.util.UUID

class BalanceData {
    fun getBalance(accountId: UUID): Result<Balance?, Throwable> =
        runCatching {
            BalanceTable
                .select(BalanceTable.id, BalanceTable.accountId, BalanceTable.balance)
                .where { BalanceTable.accountId eq accountId.toString() }
                .limit(1)
                .singleOrNull()
                ?.let { Balance.fromRow(it) }
        }

    fun setBalance(input: SetBalance): Result<Int, Throwable> =
        runCatching {
            BalanceTable.update({ BalanceTable.accountId eq input.accountId.toString() }) {
                it[BalanceTable.balance] = input.balance
            }
        }

    fun withdrawFromBalance(input: WithdrawFromBalance): Result<Int, Throwable> =
        runCatching {
            BalanceTable.update({ BalanceTable.accountId eq input.accountId.toString() }) {
                it[BalanceTable.balance] = BalanceTable.balance - input.amount
            }
        }

    fun depositIntoBalance(input: DepositIntoBalance): Result<Int, Throwable> =
        runCatching {
            BalanceTable.update({ BalanceTable.accountId eq input.accountId.toString() }) {
                it[BalanceTable.balance] = BalanceTable.balance + input.amount
            }
        }

    // TODO: Create the transaction here and call the db queries (using the data layer functions) within it.
    //  This means we wouldn't have runCatching or transaction within the data layer functions.
    // TODO: Maybe just get rid of this. Since we're using transactions we can just call the withdraw and deposit functions
    // fun transferBalance(input: TransferBalance): Result<Boolean, Throwable> {
    //    val withdrawBalanceQuery = "UPDATE te_balance b SET b.balance = b.balance - ? WHERE b.account_id = ?"
    //    val depositBalanceQuery = "UPDATE te_balance b SET b.balance = b.balance + ? WHERE b.account_id = ?"

    //    return runCatching {
    //        database.dataSource.connection.use { conn ->
    //            conn.autoCommit = false

    //            try {
    //                conn.prepareStatement(withdrawBalanceQuery).use { stmt ->
    //                    stmt.setDouble(1, input.amount)
    //                    stmt.setString(2, input.fromAccountId.toString())

    //                    stmt.executeUpdate()
    //                }

    //                conn.prepareStatement(depositBalanceQuery).use { stmt ->
    //                    stmt.setDouble(1, input.amount)
    //                    stmt.setString(2, input.toAccountId.toString())

    //                    stmt.executeUpdate()
    //                }

    //                conn.commit()
    //            } catch (e: SQLException) {
    //                conn.rollback()
    //                throw e
    //            }
    //        }

    //        true
    //    }
    // }
}
