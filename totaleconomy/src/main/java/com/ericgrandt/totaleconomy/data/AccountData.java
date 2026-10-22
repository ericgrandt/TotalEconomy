package com.ericgrandt.totaleconomy.data;

import com.ericgrandt.totaleconomy.dto.CreateAccountDto;
import com.ericgrandt.totaleconomy.model.TEAccount;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class AccountData {
    public TEAccount createAccount(Connection conn, CreateAccountDto req) throws SQLException {
        var insertQuery = "INSERT IGNORE INTO te_account(player_id, currency_code, balance) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setString(1, req.playerId().toString());
            stmt.setString(2, req.currencyCode());
            stmt.setBigDecimal(3, req.balance());
            stmt.executeUpdate();
        }
        return new TEAccount(req.playerId(), req.currencyCode(), req.balance());
    }

    public Optional<TEAccount> getAccount(Connection conn, UUID playerId, String currencyCode) throws SQLException {
        var query = "SELECT player_id, currency_code, balance FROM te_account WHERE player_id = ? AND currency_code = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, playerId.toString());
            stmt.setString(2, currencyCode);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(
                        new TEAccount(
                            UUID.fromString(rs.getString("player_id")),
                            rs.getString("currency_code"),
                            rs.getBigDecimal("balance")
                        )
                    );
                }
            }
        }

        return Optional.empty();
    }

    public boolean withdraw(
        Connection conn,
        UUID playerId,
        String currencyCode,
        BigDecimal amount,
        boolean enforceMinimumBalance
    ) throws SQLException {
        var query = "UPDATE te_account SET balance = balance - ? WHERE player_id = ? AND currency_code = ?";
        if (enforceMinimumBalance) {
            query = query + " AND balance >= ?";
        }

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBigDecimal(1, amount);
            stmt.setString(2, playerId.toString());
            stmt.setString(3, currencyCode);

            if (enforceMinimumBalance) {
                stmt.setBigDecimal(4, amount);
            }

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deposit(
        Connection conn,
        UUID playerId,
        String currencyCode,
        BigDecimal amount
    ) throws SQLException {
        var query = "UPDATE te_account SET balance = balance + ? WHERE player_id = ? AND currency_code = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBigDecimal(1, amount);
            stmt.setString(2, playerId.toString());
            stmt.setString(3, currencyCode);

            return stmt.executeUpdate() > 0;
        }
    }
}
