package com.ericgrandt.totaleconomy.testutils

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

        fun seedCurrency(): CurrencyEntity {
            val currency =
                CurrencyEntity(
                    UUID.randomUUID(),
                    "USD",
                    "Dollar",
                    "Dollars",
                    null,
                    2,
                    true,
                    Instant.parse(TEST_DATE),
                )
            transaction {
                CurrencyTable.insert {
                    it[CurrencyTable.code] = currency.code
                    it[CurrencyTable.name] = currency.name
                    it[CurrencyTable.pluralName] = currency.pluralName
                    it[CurrencyTable.symbol] = currency.symbol
                    it[CurrencyTable.fractionalDigits] = currency.fractionalDigits
                    it[CurrencyTable.isDefault] = currency.isDefault
                }
            }
            return currency
        }

        // fun seedAccount(): AccountEntity {
        //    val account = AccountEntity(UUID.randomUUID(), Instant.parse(TEST_DATE))
        //    transaction {
        //        AccountTable.insert {
        //            it[AccountTable.id] = account.id.toString()
        //            it[AccountTable.createdAt] = account.createdAt
        //        }
        //    }
        //    return account
        // }

        // fun seedBalance(
        //    accountId: UUID,
        //    balance: Balance?,
        // ): Balance {
        //    val toInsert = balance ?: Balance(UUID.randomUUID(), accountId, 1.23)

        //    transaction {
        //        BalanceTable.insert {
        //            it[BalanceTable.id] = toInsert.id.toString()
        //            it[BalanceTable.accountId] = toInsert.accountId.toString()
        //            it[BalanceTable.balance] = toInsert.balance
        //        }
        //    }

        //    return toInsert
        // }

        // fun seedBank(
        //    accountId: UUID,
        //    bank: Bank?,
        // ): Bank {
        //    val toInsert = bank ?: Bank(UUID.randomUUID(), accountId, 1.23)

        //    transaction {
        //        BankTable.insert {
        //            it[BankTable.id] = toInsert.id.toString()
        //            it[BankTable.accountId] = toInsert.accountId.toString()
        //            it[BankTable.balance] = toInsert.balance
        //        }
        //    }

        //    return toInsert
        // }

        private fun initDb() {
            transaction {
                SchemaUtils.create(CurrencyTable, AccountTable)
            }
        }
    }
}
