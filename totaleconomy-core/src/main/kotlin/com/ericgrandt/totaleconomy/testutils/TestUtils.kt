package com.ericgrandt.totaleconomy.testutils

import com.ericgrandt.totaleconomy.data.entity.AccountEntity
import com.ericgrandt.totaleconomy.data.entity.CurrencyEntity
import com.ericgrandt.totaleconomy.data.table.AccountTable
import com.ericgrandt.totaleconomy.data.table.CurrencyTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.UUID
import kotlin.time.Instant

class TestUtils {
    companion object {
        val d: HikariDataSource
        val c = HikariConfig()

        const val TEST_DATE = "2025-01-01T00:00:00Z"
        val TEST_ACCOUNT_ID_ONE: UUID = UUID.randomUUID()

        init {
            c.jdbcUrl = "jdbc:h2:mem:totaleconomy;MODE=MySQL"
            c.addDataSourceProperty("cachePrepStmts", "true")
            c.addDataSourceProperty("prepStmtCacheSize", "250")
            c.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
            d = HikariDataSource(c)
        }

        fun connectToTestDb(runInit: Boolean = true) {
            val config = HikariConfig()
            config.jdbcUrl = "jdbc:h2:mem:${UUID.randomUUID()};MODE=MySQL"

            Database.connect(HikariDataSource(config))

            if (runInit) {
                initDb()
            }
        }

        fun seedDefaultCurrency(): CurrencyEntity {
            val currency =
                CurrencyEntity(
                    1,
                    "USD",
                    "Dollar",
                    "Dollars",
                    "$",
                    2,
                    true,
                    Instant.parse(TEST_DATE),
                )
            transaction {
                CurrencyTable.insert {
                    it[CurrencyTable.id] = currency.id
                    it[CurrencyTable.code] = currency.code
                    it[CurrencyTable.name] = currency.name
                    it[CurrencyTable.pluralName] = currency.pluralName
                    it[CurrencyTable.symbol] = currency.symbol
                    it[CurrencyTable.fractionalDigits] = currency.fractionalDigits
                    it[CurrencyTable.isDefault] = currency.isDefault
                    it[CurrencyTable.createdAt] = currency.createdAt
                }
            }
            return currency
        }

        fun seedNonDefaultCurrency(): CurrencyEntity {
            val currency =
                CurrencyEntity(
                    2,
                    "COIN",
                    "Coin",
                    "Coins",
                    null,
                    0,
                    false,
                    Instant.parse(TEST_DATE),
                )
            transaction {
                CurrencyTable.insert {
                    it[CurrencyTable.id] = currency.id
                    it[CurrencyTable.code] = currency.code
                    it[CurrencyTable.name] = currency.name
                    it[CurrencyTable.pluralName] = currency.pluralName
                    it[CurrencyTable.symbol] = currency.symbol
                    it[CurrencyTable.fractionalDigits] = currency.fractionalDigits
                    it[CurrencyTable.isDefault] = currency.isDefault
                    it[CurrencyTable.createdAt] = currency.createdAt
                }
            }
            return currency
        }

        fun seedAccount(currencyCode: String): AccountEntity {
            val account = AccountEntity(1, TEST_ACCOUNT_ID_ONE, currencyCode, 10.0, Instant.parse(TEST_DATE))
            transaction {
                AccountTable
                    .insert {
                        it[AccountTable.playerId] = account.playerId.toString()
                        it[AccountTable.currencyCode] = account.currencyCode
                        it[AccountTable.balance] = account.balance
                        it[AccountTable.createdAt] = account.createdAt
                    }
            }

            return account
        }

        private fun initDb() {
            transaction {
                SchemaUtils.create(CurrencyTable, AccountTable)
            }
        }
    }
}
