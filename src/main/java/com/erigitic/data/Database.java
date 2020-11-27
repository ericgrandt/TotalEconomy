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
    private final Logger logger;
    private final String connectionString;
    private SqlService sql;

    public Database() {
        TotalEconomy plugin = TotalEconomy.getPlugin();
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
        String createBalanceTable = "CREATE TABLE IF NOT EXISTS balance (userId VARCHAR(36) NOT NULL, currencyName VARCHAR(50) NOT NULL, balance NUMERIC DEFAULT 0 NOT NULL, CONSTRAINT balance_user_FK FOREIGN KEY (userId) REFERENCES te_user(id) ON DELETE CASCADE, CONSTRAINT balance_currency_FK FOREIGN KEY (currencyName) REFERENCES currency(nameSingular) ON DELETE CASCADE, CONSTRAINT balance_UN UNIQUE KEY (userId, currencyName))";
        String createShopTable = "CREATE TABLE IF NOT EXISTS shop (id VARCHAR(36) NOT NULL, shopType TINYINT(3) unsigned NOT NULL DEFAULT 0, shopOwner VARCHAR(36) DEFAULT NULL, shopName VARCHAR(30) NOT NULL DEFAULT 'Shop', PRIMARY KEY (id), UNIQUE KEY shop_UN (shopOwner))";

        try (Connection conn = getConnection()) {
            executeQuery(conn, createDatabaseQuery);
            executeQuery(conn, setDatabaseQuery);
            executeQuery(conn, createUserTable);
            executeQuery(conn, createCurrencyTable);
            executeQuery(conn, createBalanceTable);
            executeQuery(conn, createShopTable);
        } catch (SQLException e) {
            logger.error("Error setting up database");
        }
    }

    private void executeQuery(Connection conn, String query) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.executeQuery();
        }
    }
}
