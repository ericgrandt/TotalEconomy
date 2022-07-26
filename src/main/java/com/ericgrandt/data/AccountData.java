package com.ericgrandt.data;

import org.apache.logging.log4j.Logger;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import java.sql.Connection;
import java.sql.SQLException;

public class AccountData {
    private final Logger logger;
    private final Database database;

    public AccountData(Logger logger, Database database) {
        this.logger = logger;
        this.database = database;
    }

    public void createAccount(UniqueAccount account) {
        String createAccountQuery = "INSERT INTO te_account(id) VALUES (?)";
        String createBalancesQuery = "INSERT INTO te_balance(" +
            "   user_id," +
            "   currency_id, " +
            "   balance " +
            ") SELECT " +
            "    ?, " +
            "    c.id, " +
            "    db.default_balance " +
            "FROM " +
            "    te_currency c " +
            "INNER JOIN te_default_balance db ON " +
            "    c.id = db.currency_id";
        
        try (Connection conn = database.getConnection()) {

        } catch (SQLException e) {
            logger.error(
                String.format(
                    "Error creating account (Query: %s, Parameters: %s) (Query: %s, Parameters: %s)",
                    createAccountQuery,
                    account.uniqueId(),
                    createBalancesQuery,
                    account.uniqueId()
                )
            );
        }
    }
}
