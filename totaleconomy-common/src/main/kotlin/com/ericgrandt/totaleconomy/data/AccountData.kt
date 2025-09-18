package com.ericgrandt.totaleconomy.data

import com.ericgrandt.totaleconomy.data.entity.Account
import com.ericgrandt.totaleconomy.model.Result
import java.sql.SQLException
import java.time.Instant
import java.util.UUID

class AccountData {
    val database: Database

    constructor(database: Database) {
        this.database = database
    }

    fun createAccount(accountId: UUID): Result<Boolean> {
        val createAccountQuery = "INSERT IGNORE INTO te_account(id) VALUES (?)"

        database.dataSource.connection.use { conn ->
            try {
                conn.prepareStatement(createAccountQuery).use { stmt ->
                    stmt.setString(1, accountId.toString())
                    stmt.executeUpdate()

                    return Result.Success(true)
                }
            } catch (e: SQLException) {
                return Result.Error("error creating account", e)
            }
        }
    }

    fun getAccount(accountId: UUID): Result<Account> {
        val getAccountQuery = "SELECT id, created_at FROM te_account a WHERE a.id = ?"

        database.dataSource.connection.use { conn ->
            try {
                conn.prepareStatement(getAccountQuery).use { stmt ->
                    stmt.setString(1, accountId.toString())

                    stmt.executeQuery().use { rs ->
                        if (rs.next()) {
                            return Result.Success(Account(
                                UUID.fromString(rs.getString("id")),
                                rs.getObject("created_at", Instant::class.java)
                            ))
                        }

                        return Result.Info("account not found")
                    }

                }
            } catch (e: SQLException) {
                return Result.Error("error checking for account existence", e)
            }
        }
    }
}