package com.ericgrandt.data;

import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class AccountData {
    private final Logger logger;
    private final Database database;

    public AccountData(Logger logger, Database database) {
        this.logger = logger;
        this.database = database;
    }

    public void createAccount(UUID accountId) {
        String createAccountQuery = "INSERT INTO te_account(id) VALUES (?)";

        try (Connection conn = database.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(createAccountQuery)) {
                stmt.setString(1, accountId.toString());
                stmt.execute();
            }
        } catch (SQLException e) {
            logger.error(
                String.format(
                    "Error creating account (Query: %s, Parameters: %s)",
                    createAccountQuery,
                    accountId
                )
            );
        }
    }
}
