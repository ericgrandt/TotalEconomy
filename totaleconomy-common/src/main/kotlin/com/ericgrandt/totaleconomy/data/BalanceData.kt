package com.ericgrandt.totaleconomy.data

import com.ericgrandt.totaleconomy.data.entity.Balance
import com.ericgrandt.totaleconomy.model.DepositIntoBalance
import com.ericgrandt.totaleconomy.model.SetBalance
import com.ericgrandt.totaleconomy.model.TransferBalance
import com.ericgrandt.totaleconomy.model.WithdrawFromBalance
import com.github.michaelbull.result.runCatching
import com.github.michaelbull.result.Result
import java.sql.SQLException
import java.util.UUID
import kotlin.use

class BalanceData {
    val database: Database

    constructor(database: Database) {
        this.database = database
    }

    fun getBalance(accountId: UUID): Result<Balance?, Throwable> {
        val getBalanceQuery = "SELECT b.id, b.account_id, b.balance FROM te_balance b WHERE b.account_id = ?"

        return runCatching {
            database.dataSource.connection.use { conn ->
                conn.prepareStatement(getBalanceQuery).use { stmt ->
                    stmt.setString(1, accountId.toString())

                    stmt.executeQuery().use { rs ->
                        if (rs.next()) {
                            Balance(
                                UUID.fromString(rs.getString("id")),
                                UUID.fromString(rs.getString("account_id")),
                                rs.getDouble("balance")
                            )
                        } else {
                            null
                        }
                    }
                }
            }
        }
    }

    fun setBalance(input: SetBalance): Result<Int, Throwable> {
        val setBalanceQuery = "UPDATE te_balance b SET b.balance = ? WHERE b.account_id = ?"

        return runCatching {
            database.dataSource.connection.use { conn ->
                conn.prepareStatement(setBalanceQuery).use { stmt ->
                    stmt.setDouble(1, input.balance)
                    stmt.setString(2, input.accountId.toString())

                    stmt.executeUpdate()
                }
            }
        }
    }

    fun withdrawFromBalance(input: WithdrawFromBalance): Result<Int, Throwable> {
        val setBalanceQuery = "UPDATE te_balance b SET b.balance = b.balance - ? WHERE b.account_id = ?"

        return runCatching {
            database.dataSource.connection.use { conn ->
                conn.prepareStatement(setBalanceQuery).use { stmt ->
                    stmt.setDouble(1, input.amount)
                    stmt.setString(2, input.accountId.toString())

                    stmt.executeUpdate()
                }
            }
        }
    }

    fun depositIntoBalance(input: DepositIntoBalance): Result<Int, Throwable> {
        val setBalanceQuery = "UPDATE te_balance b SET b.balance = b.balance + ? WHERE b.account_id = ?"

        return runCatching {
            database.dataSource.connection.use { conn ->
                conn.prepareStatement(setBalanceQuery).use { stmt ->
                    stmt.setDouble(1, input.amount)
                    stmt.setString(2, input.accountId.toString())

                    stmt.executeUpdate()
                }
            }
        }
    }

    fun transferBalance(input: TransferBalance): Result<Boolean, Throwable> {
        val withdrawBalanceQuery = "UPDATE te_balance b SET b.balance = b.balance - ? WHERE b.account_id = ?"
        val depositBalanceQuery = "UPDATE te_balance b SET b.balance = b.balance + ? WHERE b.account_id = ?"

        return runCatching {
            database.dataSource.connection.use { conn ->
                conn.autoCommit = false

                try {
                    conn.prepareStatement(withdrawBalanceQuery).use { stmt ->
                        stmt.setDouble(1, input.amount)
                        stmt.setString(2, input.fromAccountId.toString())

                        stmt.executeUpdate()
                    }

                    conn.prepareStatement(depositBalanceQuery).use { stmt ->
                        stmt.setDouble(1, input.amount)
                        stmt.setString(2, input.toAccountId.toString())

                        stmt.executeUpdate()
                    }

                    conn.commit()
                } catch (e: SQLException) {
                    conn.rollback()
                    throw e
                }
            }

            true
        }
    }
}