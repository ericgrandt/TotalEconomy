package com.ericgrandt.totaleconomy.data;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseBootstrapper {
    public static void initSchema(Connection conn) throws SQLException {
        createCurrencyTable(conn);
        createAccountTable(conn);
    }

    public static void initData(Connection conn) throws SQLException {
        seedDefaultCurrency(conn);
    }

    private static void createCurrencyTable(Connection conn) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS te_currency (
                id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                code VARCHAR(10) NOT NULL UNIQUE,
                name VARCHAR(20) NOT NULL UNIQUE,
                plural_name VARCHAR(21) NOT NULL UNIQUE,
                symbol VARCHAR(4),
                fractional_digits INT NOT NULL,
                starting_balance DECIMAL(10, 4) NOT NULL DEFAULT 0,
                is_default BOOLEAN NOT NULL,
                created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """;

        try (var stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void createAccountTable(Connection conn) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS te_account (
                id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                player_id VARCHAR(36) NOT NULL,
                currency_code VARCHAR(10) NOT NULL,
                balance DECIMAL(10, 4) NOT NULL DEFAULT 0,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (currency_code) REFERENCES te_currency(code) ON DELETE CASCADE,
                UNIQUE KEY uk_te_account_player_currency (player_id, currency_code)
            )
            """;

        try (var stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void seedDefaultCurrency(Connection conn) throws SQLException {
        String sql = """
            INSERT IGNORE INTO te_currency (
                code,
                name,
                plural_name,
                symbol,
                fractional_digits,
                starting_balance,
                is_default
            ) VALUES (
                'USD',
                'Dollar',
                'Dollars',
                '$',
                2,
                0,
                true
            )
            """;

        try (var stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
}
