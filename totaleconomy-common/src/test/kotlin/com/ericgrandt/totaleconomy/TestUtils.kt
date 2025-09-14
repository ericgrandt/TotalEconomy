package com.ericgrandt.totaleconomy

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

class TestUtils {
    companion object {
        val config = HikariConfig()
        val ds: HikariDataSource

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

        private fun setupDb() {

        }
    }
}