package com.ericgrandt.data;

import com.ericgrandt.data.dto.AccountDto;
import com.ericgrandt.data.dto.VirtualAccountDto;
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
                )
            );
        }

        return false;
    }

    public AccountDto getAccount(UUID uuid) {
        return null;
    }

    public boolean deleteAccount(UUID uuid) {
        return true;
    }

    public boolean createVirtualAccount(String identifier) {
        String createVirtualAccountQuery = "INSERT INTO te_virtual_account(identifier) VALUES (?)";

        try (
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(createVirtualAccountQuery)
        ) {
            stmt.setString(1, identifier);
            stmt.execute();

            return true;
        } catch (SQLException e) {
            logger.error(
                String.format(
                    "Error creating account (Query: %s, Parameters: %s)",
                    createVirtualAccountQuery,
                    identifier
                )
            );
        }

        return false;
    }

    public VirtualAccountDto getVirtualAccount(String identifier) {
        return null;
    }

    public boolean deleteVirtualAccount(String identifier) {
        return true;
    }
}
