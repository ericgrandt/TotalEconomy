package com.ericgrandt.data;

import com.ericgrandt.data.dto.AccountDto;
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

    public int createAccount(UUID accountId) throws SQLException {
        String createAccountQuery = "INSERT INTO te_account(id) VALUES (?)";

        try (
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(createAccountQuery)
        ) {
            stmt.setString(1, accountId.toString());
            return stmt.executeUpdate();
        }
    }

    public AccountDto getAccount(UUID accountId) throws SQLException {
        String getAccountQuery = "SELECT * FROM te_account WHERE id = ?";

        try (
            Connection conn = database.getConnection();
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
            Connection conn = database.getConnection();
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
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(deleteAccountQuery)
        ) {
            stmt.setString(1, accountId.toString());
            int rowsDeleted = stmt.executeUpdate();

            return rowsDeleted != 0;
        }
    }
}
