package com.ericgrandt.totaleconomy.data

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

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
        SqlScripts.initScripts.forEach { script ->
            this.dataSource.connection.use { conn ->
                conn.prepareStatement(script).use { pst ->
                    pst.execute()
                }
            }
        }
    }
}