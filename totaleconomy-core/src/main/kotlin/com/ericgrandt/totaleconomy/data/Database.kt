package com.ericgrandt.totaleconomy.data

import com.ericgrandt.totaleconomy.data.table.AccountTable
import com.ericgrandt.totaleconomy.data.table.CurrencyTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class Database {
    val url: String
    val user: String
    val password: String
    val dataSource: HikariDataSource

    constructor(url: String, user: String, password: String) {
        this.url = url
        this.user = user
        this.password = password
        this.dataSource = createDataSource()
    }

    private fun createDataSource(): HikariDataSource {
        val config = HikariConfig()
        config.jdbcUrl = this.url
        config.username = this.user
        config.password = this.password
        config.addDataSourceProperty("minimumIdle", "3")
        config.addDataSourceProperty("maximumPoolSize", "10")
        return HikariDataSource(config)
    }

    fun initDatabase() {
        transaction {
            SchemaUtils.create(CurrencyTable, AccountTable)

            CurrencyTable.insertIgnore {
                it[CurrencyTable.code] = "USD"
                it[CurrencyTable.name] = "Dollar"
                it[CurrencyTable.pluralName] = "Dollars"
                it[CurrencyTable.symbol] = "$"
                it[CurrencyTable.fractionalDigits] = 2
                it[CurrencyTable.isDefault] = true
            }
        }
    }

    fun connect() {
        Database.connect(dataSource)
    }
}
