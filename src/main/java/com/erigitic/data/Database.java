package com.erigitic.data;

import com.erigitic.TotalEconomy;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {
    private final TotalEconomy plugin;
    private final Logger logger;
    private final String connectionString;

    private SqlService sql;

    public Database() {
        plugin = TotalEconomy.getPlugin();
        logger = plugin.getLogger();
        connectionString = plugin.getDefaultConfiguration().getConnectionString();
    }

    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    private DataSource getDataSource() throws SQLException {
        if (sql == null) {
            sql = Sponge.getServiceManager().provide(SqlService.class).get();
        }

        return sql.getDataSource(connectionString);
    }

    public void setup() {
        String createDatabaseQuery = "CREATE DATABASE IF NOT EXISTS totaleconomy";
        String setDatabaseQuery = "USE totaleconomy";
        String createUserTable = "CREATE TABLE IF NOT EXISTS te_user (id VARCHAR(36) NOT NULL, CONSTRAINT user_PK PRIMARY KEY (id))";
        String createCurrencyTable = "CREATE TABLE IF NOT EXISTS currency (id INT auto_increment NOT NULL, nameSingular VARCHAR(50) NOT NULL UNIQUE, namePlural VARCHAR(50) NOT NULL UNIQUE, symbol VARCHAR(1) NOT NULL, isDefault BOOL NOT NULL, CONSTRAINT currency_PK PRIMARY KEY (id))";
        String createBalanceTable = "CREATE TABLE IF NOT EXISTS balance (userId VARCHAR(36) NOT NULL, currencyId INT NOT NULL, balance NUMERIC DEFAULT 0 NOT NULL, CONSTRAINT balance_user_FK FOREIGN KEY (userId) REFERENCES totaleconomy.te_user(id) ON DELETE CASCADE, CONSTRAINT balance_currency_FK FOREIGN KEY (currencyId) REFERENCES totaleconomy.currency(id) ON DELETE CASCADE)";

        try (Connection conn = getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement(createDatabaseQuery)) {
                stmt.executeQuery();
            }

            try(PreparedStatement stmt = conn.prepareStatement(setDatabaseQuery)) {
                stmt.executeQuery();
            }

            try(PreparedStatement stmt = conn.prepareStatement(createUserTable)) {
                stmt.executeQuery();
            }

            try(PreparedStatement stmt = conn.prepareStatement(createCurrencyTable)) {
                stmt.executeQuery();
            }

            try(PreparedStatement stmt = conn.prepareStatement(createBalanceTable)) {
                stmt.executeQuery();
            }
        } catch (SQLException e) {
            logger.error("Error setting up database");
        }
    }
}
