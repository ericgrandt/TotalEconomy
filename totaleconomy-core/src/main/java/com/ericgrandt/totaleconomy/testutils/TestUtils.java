package com.ericgrandt.totaleconomy.testutils;

import com.ericgrandt.totaleconomy.data.DatabaseBootstrapper;
import com.ericgrandt.totaleconomy.data.entity.AccountEntity;
import com.ericgrandt.totaleconomy.data.entity.CurrencyEntity;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public class TestUtils {
    private static final String TEST_DATE = "2025-01-01T00:00:00Z";

    public static HikariDataSource startTestDb(boolean runInit) throws SQLException {
        var config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL");

        var dataSource = new HikariDataSource(config);
        var conn = dataSource.getConnection();
        if (runInit) {
            DatabaseBootstrapper.initSchema(conn);
        }

        return dataSource;
    }

    public static CurrencyEntity seedDefaultCurrency(HikariDataSource dataSource) throws SQLException {
        CurrencyEntity currency = new CurrencyEntity(
            1,
            "USD",
            "Dollar",
            "Dollars",
            "$",
            2,
            true,
            Instant.parse(TEST_DATE)
        );
        String query = """
            INSERT IGNORE INTO te_currency (
                code,
                name,
                plural_name,
                symbol,
                fractional_digits,
                is_default
            ) VALUES ('%s', '%s', '%s', '%s', %d, %b)"""
            .formatted(
                currency.code(),
                currency.name(),
                currency.pluralName(),
                currency.symbol(),
                currency.fractionalDigits(),
                currency.isDefault()
            );


        try (Connection conn = dataSource.getConnection()) {
            try (var stmt = conn.createStatement()) {
                stmt.execute(query);
            }
        }

        return currency;
    }

    public static CurrencyEntity seedCurrency(HikariDataSource dataSource) throws SQLException {
        CurrencyEntity currency = new CurrencyEntity(
            2,
            "COIN",
            "Coin",
            "Coins",
            null,
            0,
            false,
            Instant.parse(TEST_DATE)
        );
        String query = """
            INSERT IGNORE INTO te_currency (
                code,
                name,
                plural_name,
                symbol,
                fractional_digits,
                is_default
            ) VALUES (?, ?, ?, ?, ?, ?)""";

        try (Connection conn = dataSource.getConnection()) {
            try (var stmt = conn.prepareStatement(query)) {
                stmt.setString(1, currency.code());
                stmt.setString(2, currency.name());
                stmt.setString(3, currency.pluralName());
                stmt.setString(4, currency.symbol());
                stmt.setInt(5, currency.fractionalDigits());
                stmt.setBoolean(6, currency.isDefault());
                stmt.execute();
            }
        }

        return currency;
    }

    public static AccountEntity seedAccount(HikariDataSource dataSource, String currencyCode) throws SQLException {
        AccountEntity account = new AccountEntity(
            1,
            UUID.randomUUID().toString(),
            currencyCode == null ? "USD" : currencyCode,
            BigDecimal.TEN,
            Instant.parse(TEST_DATE)
        );
        String query = """
            INSERT IGNORE INTO te_account (
                player_id,
                currency_code,
                balance
            ) VALUES (?, ?, ?)""";

        try (Connection conn = dataSource.getConnection()) {
            try (var stmt = conn.prepareStatement(query)) {
                stmt.setString(1, account.playerId());
                stmt.setString(2, account.currencyCode());
                stmt.setBigDecimal(3, account.balance());
                stmt.execute();
            }
        }

        return account;
    }
}