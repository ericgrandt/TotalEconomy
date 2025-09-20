package com.ericgrandt.totaleconomy.data

import com.ericgrandt.totaleconomy.data.entity.Account
import com.ericgrandt.totaleconomy.data.entity.Balance
import com.ericgrandt.totaleconomy.result.Result
import com.ericgrandt.totaleconomy.result.runOrCatch
import java.time.Instant
import java.util.UUID
import kotlin.use

class BalanceData {
    val database: Database

    constructor(database: Database) {
        this.database = database
    }

    fun getBalance(accountId: UUID): Result<Balance?, Throwable> {
        val getBalanceQuery = "SELECT id, account_id, balance FROM te_balance b WHERE b.account_id = ?"

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
}