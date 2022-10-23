package com.ericgrandt.data;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BalanceData {
    private final Database database;

    public BalanceData(Database database) {
        this.database = database;
    }

    public BigDecimal getDefaultBalance(int currencyId) throws SQLException {
        String getDefaultBalanceQuery = "SELECT default_balance FROM te_default_balance WHERE currency_id = ?";

        try (
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(getDefaultBalanceQuery)
        ) {
            stmt.setInt(1, currencyId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("default_balance");
                }
            }
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal getBalance(String accountId, int currencyId) throws SQLException {
        String getDefaultBalanceQuery = "SELECT balance FROM te_balance WHERE account_id = ? AND currency_id = ?";

        try (
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(getDefaultBalanceQuery)
        ) {
            stmt.setString(1, accountId);
            stmt.setInt(2, currencyId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("balance");
                }
            }
        }

        return null;
    }
}
