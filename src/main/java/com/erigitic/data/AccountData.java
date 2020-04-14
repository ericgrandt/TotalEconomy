package com.erigitic.data;

import com.erigitic.TotalEconomy;
import com.erigitic.economy.TEAccount;
import com.erigitic.economy.TECurrency;
import org.slf4j.Logger;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;

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

    public void createAccount(String uuid) {
        String createUserQuery = "INSERT INTO te_user VALUES (?)";
        String createBalancesQuery = "INSERT INTO balance(userId, currencyName, balance) SELECT ?, nameSingular, 0 FROM currency";

        try (Connection conn = database.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement(createUserQuery)) {
                stmt.setString(1, uuid);
                stmt.execute();
            }

            try(PreparedStatement stmt = conn.prepareStatement(createBalancesQuery)) {
                stmt.setString(1, uuid);
                stmt.execute();
            }
        } catch (SQLException e) {
            logger.error(String.format("Error creating account (Query: %s, Parameters: %s) (Query: %s, Parameters: %s)", createUserQuery, uuid.toString(), createBalancesQuery, uuid.toString()));
        }
    }

    public Optional<UniqueAccount> getAccount(String uuid) {
        String query = "SELECT id FROM te_user WHERE id = ? LIMIT 1";

        try (Connection conn = database.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, uuid);

                ResultSet results = stmt.executeQuery();
                if (results.next()) {
                    UniqueAccount account = new TEAccount(
                        UUID.fromString(results.getString("id"))
                    );

                    return Optional.of(account);
                }
            }
        } catch (SQLException e) {
            logger.error(String.format("Error getting account (Query: %s, Parameters: %s)", query, uuid.toString()));
        }

        return Optional.empty();
    }

    public BigDecimal getBalance(String currencyIdentifier, String uuid) {
        String query = "SELECT balance FROM balance WHERE currencyName = ? AND userId = ?";

        try (Connection conn = database.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, currencyIdentifier);
                stmt.setString(2, uuid);

                ResultSet results = stmt.executeQuery();
                if (results.next()) {
                    return results.getBigDecimal("balance");
                }
            }
        } catch (SQLException e) {
            logger.error(String.format("Error getting balance from database (Query: %s, Parameters: %s, %s)", query, currencyIdentifier, uuid.toString()));
        }

        return null;
    }

    public Map<Currency, BigDecimal> getBalances(String uuid) {
        String query = "SELECT balance, currencyName, currency.namePlural, currency.symbol, currency.isDefault FROM balance INNER JOIN currency ON currency.nameSingular = balance.currencyName WHERE userId = ?";

        try (Connection conn = database.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, uuid);

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
            logger.error(String.format("Error getting balance from database (Query: %s, Parameters: %s)", query, uuid.toString()));
        }

        return new HashMap<>();
    }

    public int setBalance(String currencyIdentifier, String uuid, BigDecimal balance) {
        String query = "UPDATE balance SET balance = ? WHERE currencyName = ? AND userId = ?";

        try (Connection conn = database.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, balance.toString());
                stmt.setString(2, currencyIdentifier);
                stmt.setString(3, uuid);

                return stmt.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error(String.format("Error setting balance (Query: %s, Parameters: %d, %s, %s)", query, balance, currencyIdentifier, uuid.toString()));
        }

        return 0;
    }

    public int transfer(String currencyIdentifier, String fromUuid, String toUuid, BigDecimal fromBalance, BigDecimal toBalance) {
        String query = "UPDATE balance SET balance = ? WHERE currencyName = ? AND userId = ?";

        try (Connection conn = database.getConnection()) {
            int updatedRows = 0;
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, fromBalance.toString());
                stmt.setString(2, currencyIdentifier);
                stmt.setString(3, fromUuid);

                updatedRows += stmt.executeUpdate();
            } catch (SQLException e) {
                conn.rollback();
                logger.error(String.format("Error setting balance (Query: %s, Parameters: %d, %s, %s)", query, fromBalance, currencyIdentifier, fromUuid));
            }

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, toBalance.toString());
                stmt.setString(2, currencyIdentifier);
                stmt.setString(3, toUuid);

                updatedRows += stmt.executeUpdate();
            } catch (SQLException e) {
                conn.rollback();
                logger.error(String.format("Error setting balance (Query: %s, Parameters: %d, %s, %s)", query, toBalance, currencyIdentifier, toUuid));
            }

            conn.commit();
            conn.setAutoCommit(true);

            return updatedRows;
        } catch (SQLException e) {
            logger.error("Error setting balance");
        }

        return 0;
    }
}
