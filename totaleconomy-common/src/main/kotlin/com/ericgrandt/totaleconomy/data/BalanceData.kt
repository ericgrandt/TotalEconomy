package com.ericgrandt.totaleconomy.data

import com.ericgrandt.totaleconomy.data.entity.Balance
import com.ericgrandt.totaleconomy.model.SetBalance
import com.ericgrandt.totaleconomy.model.WithdrawFromBalance
import com.ericgrandt.totaleconomy.result.Result
import com.ericgrandt.totaleconomy.result.runOrCatch
import java.util.UUID
import kotlin.use

class BalanceData {
    val database: Database

    constructor(database: Database) {
        this.database = database
    }

    fun getBalance(accountId: UUID): Result<Balance?, Throwable> {
        val getBalanceQuery = "SELECT b.id, b.account_id, b.balance FROM te_balance b WHERE b.account_id = ?"

        return runOrCatch {
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

        return runOrCatch {
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

        return runOrCatch {
            database.dataSource.connection.use { conn ->
                conn.prepareStatement(setBalanceQuery).use { stmt ->
                    stmt.setDouble(1, input.amount)
                    stmt.setString(2, input.accountId.toString())

                    stmt.executeUpdate()
                }
            }
        }
    }
}