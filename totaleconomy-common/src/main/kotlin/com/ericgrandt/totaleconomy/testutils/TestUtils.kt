package com.ericgrandt.totaleconomy.testutils

import com.ericgrandt.totaleconomy.data.entity.Account
import com.ericgrandt.totaleconomy.data.entity.Balance
import com.ericgrandt.totaleconomy.data.table.AccountTable
import com.ericgrandt.totaleconomy.data.table.BalanceTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.sql.Connection
import java.util.UUID
import kotlin.time.Instant

class TestUtils {
    companion object {
        val d: HikariDataSource
        val c = HikariConfig()

        const val TEST_DATE = "2025-01-01T00:00:00Z"

        init {
            c.jdbcUrl = "jdbc:h2:mem:totaleconomy;MODE=MySQL"
            c.addDataSourceProperty("cachePrepStmts", "true")
            c.addDataSourceProperty("prepStmtCacheSize", "250")
            c.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
            d = HikariDataSource(c)
        }

        fun getConnection(): Connection = d.connection

        fun connectToTestDb(runInit: Boolean = true) {
            val config = HikariConfig()
            config.jdbcUrl = "jdbc:h2:mem:${UUID.randomUUID()};MODE=MySQL"

            Database.connect(HikariDataSource(config))

            if (runInit) {
                initDb()
            }
        }

        fun resetDb() {
            val queries =
                arrayOf(
                    "DELETE FROM te_account",
                )

            this.d.connection.use { conn ->
                queries.forEach { query ->
                    conn.prepareStatement(query).use { stmt ->
                        stmt.execute()
                    }
                }
            }
        }

        fun seedAccount(): Account {
            val account = Account(UUID.randomUUID(), Instant.parse(TEST_DATE))
            transaction {
                AccountTable.insert {
                    it[AccountTable.id] = account.id.toString()
                    it[AccountTable.createdAt] = account.createdAt
                }
            }
            return account
        }

        fun seedBalance(
            accountId: UUID,
            balance: Balance?,
        ): Balance {
            val toInsert = balance ?: Balance(UUID.randomUUID(), accountId, 1.23)

            transaction {
                BalanceTable.insert {
                    it[BalanceTable.id] = toInsert.id.toString()
                    it[BalanceTable.accountId] = toInsert.accountId.toString()
                    it[BalanceTable.balance] = toInsert.balance
                }
            }

            return toInsert
        }

        private fun initDb() {
            transaction {
                SchemaUtils.create(AccountTable, BalanceTable)
            }
        }
    }
}