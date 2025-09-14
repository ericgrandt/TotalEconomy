package com.ericgrandt.totaleconomy.data

import java.sql.SQLException
import java.util.*

class AccountData {
    val database: Database

    constructor(database: Database) {
        this.database = database
    }

    // TODO: Test
    fun createAccount(accountId: UUID): Boolean {
        val createAccountQuery = "INSERT IGNORE INTO te_account(id) VALUES (?)"

        database.dataSource.connection.use { conn ->
            conn.autoCommit = false

            try {
                conn.prepareStatement(createAccountQuery).use { accountStmt ->
                    accountStmt.setString(1, accountId.toString())
                    accountStmt.executeUpdate()

                    conn.commit()
                    return true
                }
            } catch (e: SQLException) {
                conn.rollback()
                throw e
            } finally {
                conn.autoCommit = true
            }
        }
    }
}