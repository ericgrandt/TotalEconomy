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
    private static final UUID TEST_ACCOUNT_ID_ONE = UUID.randomUUID();

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

    public static AccountEntity seedAccount(HikariDataSource dataSource) throws SQLException {
        AccountEntity account = new AccountEntity(
            1,
            UUID.randomUUID().toString(),
            "USD",
            BigDecimal.TEN,
            Instant.parse(TEST_DATE)
        );
        String query = """
            INSERT IGNORE INTO te_account (
                player_id,
                currency_code,
                balance
            ) VALUES ('%s', '%s', %f)"""
            .formatted(
                account.playerId(),
                account.currencyCode(),
                account.balance()
            );


        try (Connection conn = dataSource.getConnection()) {
            try (var stmt = conn.createStatement()) {
                stmt.execute(query);
            }
        }

        return account;
    }
}