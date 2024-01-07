package com.ericgrandt.totaleconomy.common.data;

import com.ericgrandt.totaleconomy.common.data.dto.AccountDto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccountData {
    private final Database database;

    public AccountData(Database database) {
        this.database = database;
    }

    public boolean createAccount(UUID accountId, int currencyId) throws SQLException {
        String createAccountQuery = "INSERT INTO te_account(id) VALUES (?)";
        String createBalanceQuery = "INSERT INTO te_balance(account_id, currency_id, balance) "
            + "SELECT ?, ?, default_balance "
            + "FROM te_default_balance tdf "
            + "WHERE tdf.currency_id = ?";

        try (Connection conn = database.getDataSource().getConnection()) {
            conn.setAutoCommit(false);

            try (
                PreparedStatement accountStmt = conn.prepareStatement(createAccountQuery);
                PreparedStatement balanceStmt = conn.prepareStatement(createBalanceQuery)
            ) {
                accountStmt.setString(1, accountId.toString());
                accountStmt.executeUpdate();

                balanceStmt.setString(1, accountId.toString());
                balanceStmt.setInt(2, currencyId);
                balanceStmt.setInt(3, currencyId);
                balanceStmt.executeUpdate();

                conn.commit();

                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public AccountDto getAccount(UUID accountId) throws SQLException {
        String getAccountQuery = "SELECT * FROM te_account WHERE id = ?";

        try (
            Connection conn = database.getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(getAccountQuery)
        ) {
            stmt.setString(1, accountId.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AccountDto(
                        rs.getString("id"),
                        rs.getTimestamp("created")
                    );
                }
            }
        }

        return null;
    }

    public List<AccountDto> getAccounts() throws SQLException {
        String getAccountQuery = "SELECT * FROM te_account";

        try (
            Connection conn = database.getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(getAccountQuery)
        ) {
            List<AccountDto> accounts = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(
                        new AccountDto(
                            rs.getString("id"),
                            rs.getTimestamp("created")
                        )
                    );
                }
            }

            return accounts;
        }
    }

    public boolean deleteAccount(UUID accountId) throws SQLException {
        String deleteAccountQuery = "DELETE FROM te_account WHERE id = ?";

        try (
            Connection conn = database.getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(deleteAccountQuery)
        ) {
            stmt.setString(1, accountId.toString());
            int rowsDeleted = stmt.executeUpdate();

            return rowsDeleted != 0;
        }
    }
}
