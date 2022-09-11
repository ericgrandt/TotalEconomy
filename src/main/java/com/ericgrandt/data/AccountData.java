package com.ericgrandt.data;

import com.ericgrandt.data.dto.AccountDto;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AccountData {
    private final Logger logger;
    private final Database database;

    public AccountData(Logger logger, Database database) {
        this.logger = logger;
        this.database = database;
    }

    public boolean createAccount(UUID accountId) {
        String createAccountQuery = "INSERT INTO te_account(id) VALUES (?)";

        try (
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(createAccountQuery)
        ) {
            stmt.setString(1, accountId.toString());
            stmt.execute();

            return true;
        } catch (SQLException e) {
            logger.error(
                String.format(
                    "Error creating account (Query: %s, Parameters: %s)",
                    createAccountQuery,
                    accountId
                ),
                e
            );
        }

        return false;
    }

    public AccountDto getAccount(UUID accountId) {
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
        } catch (SQLException e) {
            logger.error(
                String.format(
                    "Error getting account (Query: %s, Parameters: %s)",
                    getAccountQuery,
                    accountId
                ),
                e
            );
        }

        return null;
    }

    public boolean deleteAccount(UUID accountId) {
        String deleteAccountQuery = "DELETE FROM te_account WHERE id = ?";

        try (
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(deleteAccountQuery)
        ) {
            stmt.setString(1, accountId.toString());
            int rowsDeleted = stmt.executeUpdate();

            return rowsDeleted != 0;
        } catch (SQLException e) {
            logger.error(
                String.format(
                    "Error deleting account (Query: %s, Parameters: %s)",
                    deleteAccountQuery,
                    accountId
                ),
                e
            );
        }

        return false;
    }
}
