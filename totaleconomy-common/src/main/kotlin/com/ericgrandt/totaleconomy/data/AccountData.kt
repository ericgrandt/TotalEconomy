package com.ericgrandt.totaleconomy.data

import com.ericgrandt.totaleconomy.data.entity.Account
import com.ericgrandt.totaleconomy.result.Result
import com.ericgrandt.totaleconomy.result.runOrCatch
import java.time.Instant
import java.util.UUID

class AccountData {
    val database: Database

    constructor(database: Database) {
        this.database = database
    }

    fun createAccount(accountId: UUID): Result<Int, Throwable> {
        val createAccountQuery = "INSERT IGNORE INTO te_account(id) VALUES (?)"

        return runOrCatch {
            database.dataSource.connection.use { conn ->
                conn.prepareStatement(createAccountQuery).use { stmt ->
                    stmt.setString(1, accountId.toString())
                    stmt.executeUpdate()
                }
            }
        }
    }

    fun getAccount(accountId: UUID): Result<Account?, Throwable> {
        val getAccountQuery = "SELECT id, created_at FROM te_account a WHERE a.id = ?"

        return runOrCatch {
            database.dataSource.connection.use { conn ->
                conn.prepareStatement(getAccountQuery).use { stmt ->
                    stmt.setString(1, accountId.toString())

                    stmt.executeQuery().use { rs ->
                        if (rs.next()) {
                            Account(
                                UUID.fromString(rs.getString("id")),
                                rs.getObject("created_at", Instant::class.java)
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