package com.ericgrandt.totaleconomy.common.data;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class BalanceData {
    private final Database database;

    public BalanceData(Database database) {
        this.database = database;
    }

    public BigDecimal getBalance(UUID accountId, int currencyId) throws SQLException {
        String getDefaultBalanceQuery = "SELECT balance FROM te_balance WHERE account_id = ? AND currency_id = ?";

        try (
            Connection conn = database.getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(getDefaultBalanceQuery)
        ) {
            stmt.setString(1, accountId.toString());
            stmt.setInt(2, currencyId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("balance");
                }
            }
        }

        return null;
    }

    public int updateBalance(UUID accountId, int currencyId, double balance) throws SQLException {
        String updateBalanceQuery = "UPDATE te_balance SET balance = ? WHERE account_id = ? AND currency_id = ?";

        try (
            Connection conn = database.getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(updateBalanceQuery)
        ) {
            stmt.setBigDecimal(1, BigDecimal.valueOf(balance));
            stmt.setString(2, accountId.toString());
            stmt.setInt(3, currencyId);

            return stmt.executeUpdate();
        }
    }

    public void transfer(UUID fromAccountId, UUID toAccountId, int currencyId, double amount) throws SQLException {
        String withdrawQuery = "UPDATE te_balance SET balance = balance - ? WHERE account_id = ? AND currency_id = ?";
        String depositQuery = "UPDATE te_balance SET balance = balance + ? WHERE account_id = ? AND currency_id = ?";

        try (
            Connection conn = database.getDataSource().getConnection()
        ) {
            conn.setAutoCommit(false);

            try (
                PreparedStatement withdrawStmt = conn.prepareStatement(withdrawQuery);
                PreparedStatement updateStmt = conn.prepareStatement(depositQuery)
            ) {
                withdrawStmt.setBigDecimal(1, BigDecimal.valueOf(amount));
                withdrawStmt.setString(2, fromAccountId.toString());
                withdrawStmt.setInt(3, currencyId);
                withdrawStmt.executeUpdate();

                updateStmt.setBigDecimal(1, BigDecimal.valueOf(amount));
                updateStmt.setString(2, toAccountId.toString());
                updateStmt.setInt(3, currencyId);
                updateStmt.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
