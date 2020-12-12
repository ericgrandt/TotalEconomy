package com.erigitic.data;

import com.erigitic.economy.TEAccount;
import com.erigitic.economy.TECurrency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class AccountService {
    private final Logger logger = LoggerFactory.getLogger("TotalEconomy");
    private final Database database;

    public AccountService(Database database) {
        this.database = database;
    }

    public void createAccount(String uuid) {
        String createUserQuery = "INSERT INTO te_user VALUES (?)";
        String createBalancesQuery = "INSERT INTO te_balance(user_id, currency_id, balance) SELECT ?, id, 0 FROM te_currency";

        try (Connection conn = database.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(createUserQuery)) {
                stmt.setString(1, uuid);
                stmt.execute();
            }

            try (PreparedStatement stmt = conn.prepareStatement(createBalancesQuery)) {
                stmt.setString(1, uuid);
                stmt.execute();
            }
        } catch (SQLException e) {
            logger.error(String.format("Error creating account (Query: %s, Parameters: %s) (Query: %s, Parameters: %s)", createUserQuery, uuid, createBalancesQuery, uuid));
        }
    }

    public Optional<UniqueAccount> getAccount(String uuid) {
        String query = "SELECT id FROM te_user WHERE id = ? LIMIT 1";

        try (Connection conn = database.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
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
            logger.error(e.getMessage());
            logger.error(String.format("Error getting account (Query: %s, Parameters: %s)", query, uuid));
        }

        return Optional.empty();
    }

    public BigDecimal getBalance(String currencyId, String uuid) {
        String query = "SELECT balance FROM te_balance WHERE currency_id = ? AND user_id = ?";

        try (Connection conn = database.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, currencyId);
                stmt.setString(2, uuid);

                ResultSet results = stmt.executeQuery();
                if (results.next()) {
                    return results.getBigDecimal("balance");
                }
            }
        } catch (SQLException e) {
            logger.error(String.format("Error getting balance from database (Query: %s, Parameters: %s, %s)", query, currencyId, uuid));
        }

        return BigDecimal.ZERO;
    }

    public Map<Currency, BigDecimal> getBalances(String uuid) {
        String query = "SELECT balance, currency_id, c.name_plural, c.symbol FROM te_balance AS b INNER JOIN te_currency AS c ON c.id = b.currency_id WHERE user_id = ?;";

        try (Connection conn = database.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, uuid);

                ResultSet results = stmt.executeQuery();
                Map<Currency, BigDecimal> balances = new HashMap<>();
                while (results.next()) {
                    BigDecimal balance = results.getBigDecimal("balance");
                    Currency currency = new TECurrency(
                        results.getInt("id"),
                        Text.of(results.getString("name_singular")),
                        Text.of(results.getString("name_plural")),
                        Text.of(results.getString("symbol")),
                        results.getBoolean("prefix_symbol"),
                        results.getBoolean("is_default")
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

    public int setBalance(String currencyId, String uuid, BigDecimal balance) {
        String query = "UPDATE te_balance SET balance = ? WHERE currency_id = ? AND user_id = ?";

        try (Connection conn = database.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, balance.toString());
                stmt.setString(2, currencyId);
                stmt.setString(3, uuid);

                return stmt.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error(String.format("Error setting balance (Query: %s, Parameters: %.0f, %s, %s)", query, balance, currencyId, uuid));
        }

        return 0;
    }

    public int transfer(String currencyIdentifier, String fromUuid, String toUuid, BigDecimal fromBalance, BigDecimal toBalance) {
        String query = "UPDATE te_balance SET balance = ? WHERE currency_id = ? AND user_id = ?";

        try (Connection conn = database.getConnection()) {
            int updatedRows = 0;
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, fromBalance.toString());
                stmt.setString(2, currencyIdentifier);
                stmt.setString(3, fromUuid);

                updatedRows += stmt.executeUpdate();

                stmt.setString(1, toBalance.toString());
                stmt.setString(2, currencyIdentifier);
                stmt.setString(3, toUuid);

                updatedRows += stmt.executeUpdate();
            } catch (SQLException e) {
                conn.rollback();
                logger.error(String.format(
                    "Error setting balance (Query: %s, Parameters: (fromBalance: %.0f, toBalance: %.0f), %s, %s)",
                    query,
                    fromBalance,
                    toBalance,
                    currencyIdentifier,
                    fromUuid)
                );
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
