package com.erigitic.data;

import com.erigitic.TotalEconomy;
import com.erigitic.economy.TEAccount;
import com.erigitic.economy.TECurrency;
import org.slf4j.Logger;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AccountData {
    private final TotalEconomy plugin;
    private final Logger logger;
    private final Database database;

    public AccountData(Database database) {
        this.database = database;
        plugin = TotalEconomy.getPlugin();
        logger = plugin.getLogger();
    }

    public void createAccount(UUID uuid) {
        String createUserQuery = "INSERT INTO te_user VALUES (?)";
        String createBalancesQuery = "INSERT INTO balance(userId, currencyName, balance) SELECT ?, nameSingular, 0 FROM currency";

        try (Connection conn = database.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement(createUserQuery)) {
                stmt.setString(1, uuid.toString());
                stmt.executeQuery();
            }

            try(PreparedStatement stmt = conn.prepareStatement(createBalancesQuery)) {
                stmt.setString(1, uuid.toString());
                stmt.executeQuery();
            }
        } catch (SQLException e) {
            logger.error(String.format("Error creating account (Query: %s, Parameters: %s) (Query: %s, Parameters: %s)", createUserQuery, uuid, createBalancesQuery, uuid));
        }
    }

    public Optional<UniqueAccount> getAccount(UUID uuid) {
        String query = "SELECT id FROM te_user WHERE id = ? LIMIT 1";

        try (Connection conn = database.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, uuid.toString());

                ResultSet results = stmt.executeQuery();
                if (results.next()) {
                    UniqueAccount account = new TEAccount(
                        UUID.fromString(results.getString("id"))
                    );

                    return Optional.of(account);
                }
            }
        } catch (SQLException e) {
            logger.error(String.format("Error getting account (Query: %s, Parameters: %s)", query, uuid));
        }

        return Optional.empty();
    }

    public BigDecimal getBalance(String currencyIdentifier, UUID uuid) {
        String query = "SELECT balance FROM balance WHERE currencyName = ? AND userId = ?";

        try (Connection conn = database.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, currencyIdentifier);
                stmt.setString(2, uuid.toString());

                ResultSet results = stmt.executeQuery();
                if (results.next()) {
                    return results.getBigDecimal("balance");
                }
            }
        } catch (SQLException e) {
            logger.error(String.format("Error getting balance from database (Query: %s, Parameters: %s, %s)", query, currencyIdentifier, uuid));
        }

        return null;
    }

    public Map<Currency, BigDecimal> getBalances(UUID uuid) {
        String query = "SELECT balance, currencyName, currency.namePlural, currency.symbol, currency.isDefault FROM balance INNER JOIN currency ON currency.nameSingular = balance.currencyName WHERE userId = ?";

        try (Connection conn = database.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, uuid.toString());

                ResultSet results = stmt.executeQuery();

                Map<Currency, BigDecimal> balances = new HashMap<>();
                while (results.next()) {
                    BigDecimal balance = results.getBigDecimal("balance");
                    Currency currency = new TECurrency(
                        Text.of(results.getString("currencyName")),
                        Text.of(results.getString("namePlural")),
                        Text.of(results.getString("symbol").charAt(0)),
                        results.getBoolean("isDefault")
                    );

                    balances.put(currency, balance);
                }

                return balances;
            }
        } catch (SQLException e) {
            logger.error(String.format("Error getting balance from database (Query: %s, Parameters: %s)", query, uuid));
        }

        return new HashMap<>();
    }
}
