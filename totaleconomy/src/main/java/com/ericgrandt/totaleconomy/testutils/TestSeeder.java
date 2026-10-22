package com.ericgrandt.totaleconomy.testutils;

import com.ericgrandt.totaleconomy.data.entity.AccountEntity;
import com.ericgrandt.totaleconomy.data.entity.CurrencyEntity;
import com.zaxxer.hikari.HikariDataSource;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public class TestSeeder {
    private static final String TEST_DATE = "2025-01-01T00:00:00Z";

    public static CurrencyEntity seedDefaultCurrency(HikariDataSource dataSource) throws SQLException {
        CurrencyEntity currency = new CurrencyEntity(
            1,
            "USD",
            "Dollar",
            "Dollars",
            "$",
            2,
            BigDecimal.TEN,
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
                starting_balance,
                is_default
            ) VALUES (?, ?, ?, ?, ?, ?, ?)""";

        try (Connection conn = dataSource.getConnection()) {
            try (var stmt = conn.prepareStatement(query)) {
                stmt.setString(1, currency.code());
                stmt.setString(2, currency.name());
                stmt.setString(3, currency.pluralName());
                stmt.setString(4, currency.symbol());
                stmt.setInt(5, currency.fractionalDigits());
                stmt.setBigDecimal(6, currency.startingBalance());
                stmt.setBoolean(7, currency.isDefault());
                stmt.execute();
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
            BigDecimal.TEN,
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
                starting_balance,
                is_default
            ) VALUES (?, ?, ?, ?, ?, ?, ?)""";

        try (Connection conn = dataSource.getConnection()) {
            try (var stmt = conn.prepareStatement(query)) {
                stmt.setString(1, currency.code());
                stmt.setString(2, currency.name());
                stmt.setString(3, currency.pluralName());
                stmt.setString(4, currency.symbol());
                stmt.setInt(5, currency.fractionalDigits());
                stmt.setBigDecimal(6, currency.startingBalance());
                stmt.setBoolean(7, currency.isDefault());
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
