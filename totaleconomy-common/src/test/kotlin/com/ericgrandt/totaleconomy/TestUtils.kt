package com.ericgrandt.totaleconomy

import com.ericgrandt.totaleconomy.data.TestSqlScripts
import com.ericgrandt.totaleconomy.data.entity.Account
import com.ericgrandt.totaleconomy.data.entity.Balance
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import java.time.Instant
import java.util.UUID
import kotlin.use

class TestUtils {
    companion object {
        val config = HikariConfig()
        val ds: HikariDataSource

        const val TEST_DATE = "2025-01-01T00:00:00Z"

        init {
            config.jdbcUrl = "jdbc:h2:mem:totaleconomy;MODE=MySQL";
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            ds = HikariDataSource(config);
            setupDb();
        }

        fun getConnection(): Connection {
            return ds.connection
        }

        fun resetDb() {
            val queries = arrayOf(
                "DELETE FROM te_account"
            )

            this.ds.connection.use { conn ->
                queries.forEach { query ->
                    conn.prepareStatement(query).use { stmt ->
                        stmt.execute()
                    }
                }
            }
        }

        fun seedAccount(account: Account?) : Account {
            val query = "INSERT INTO te_account VALUES(?, ?)"

            val toInsert = Account(UUID.randomUUID(), Instant.parse(TEST_DATE))

            getConnection().use { conn ->
                conn.prepareStatement(query).use { stmt ->
                    stmt.setString(1, toInsert.id.toString())
                    stmt.setObject(2, toInsert.createdAt)
                    stmt.execute()
                }
            }
            return toInsert
        }

        fun seedBalance(accountId: UUID, balance: Balance?) : Balance {
            val query = "INSERT INTO te_balance VALUES(?, ?, ?)"

            val toInsert = balance ?: Balance(UUID.randomUUID(), accountId, 1.23)

            getConnection().use { conn ->
                conn.prepareStatement(query).use { stmt ->
                    stmt.setString(1, toInsert.id.toString())
                    stmt.setString(2, toInsert.accountId.toString())
                    stmt.setDouble(3, toInsert.balance)
                    stmt.execute()
                }
            }
            return toInsert
        }

        private fun setupDb() {
            TestSqlScripts.initScripts.forEach { script ->
                this.ds.connection.use { conn ->
                    conn.prepareStatement(script).use { pst ->
                        pst.execute()
                    }
                }
            }
        }
    }
}