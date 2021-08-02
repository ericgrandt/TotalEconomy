package com.ericgrandt.data;

import com.ericgrandt.domain.Balance;
import com.ericgrandt.domain.TEAccount;
import com.ericgrandt.domain.TECurrency;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.logging.log4j.Logger;

public class AccountData {
    private final Logger logger;
    private final Database database;

    public AccountData(Logger logger, Database database) {
        this.logger = logger;
        this.database = database;
    }

    public void addAccount(TEAccount account) {
        String createUserQuery = "INSERT INTO te_user VALUES (?, ?)";
        String createBalancesQuery = "INSERT INTO te_balance(user_id, currency_id, balance) SELECT ?, id, 0 FROM te_currency";

        try (Connection conn = database.getConnection()) {
            String displayName = PlainTextComponentSerializer.plainText().serialize(account.displayName());

            try (PreparedStatement stmt = conn.prepareStatement(createUserQuery)) {
                stmt.setString(1, account.uniqueId().toString());
                stmt.setString(2, displayName);
                stmt.execute();
            }

            try (PreparedStatement stmt = conn.prepareStatement(createBalancesQuery)) {
                stmt.setString(1, account.uniqueId().toString());
                stmt.execute();
            }
        } catch (SQLException e) {
            logger.error(
                String.format(
                    "Error creating account (Query: %s, Parameters: %s) (Query: %s, Parameters: %s)",
                    createUserQuery,
                    account.uniqueId(),
                    createBalancesQuery,
                    account.uniqueId()
                )
            );
        }
    }

    public boolean hasAccount(UUID uuid) {
        String query = "SELECT COUNT(1) AS user_count FROM te_user WHERE id = ?";

        try (Connection conn = database.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, uuid.toString());

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("user_count") > 0;
                }
            }
        } catch (SQLException e) {
            logger.error(String.format("Error checking for account existence (Query: %s, Parameters: %s)", query, uuid.toString()));
        }

        return false;
    }

    public TEAccount getAccount(UUID uuid) {
        String query = "SELECT tu.id, display_name, currency_id, balance, name_singular, name_plural, symbol, num_fraction_digits, is_default\n"
            + "FROM te_user tu\n"
            + "INNER JOIN te_balance tb ON\n"
            + "tu.id = tb.user_id\n"
            + "INNER JOIN te_currency tc ON\n"
            + "tc.id = tb.currency_id\n"
            + "WHERE tu.id = ?";

        try (Connection conn = database.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, uuid.toString());

                ResultSet results = stmt.executeQuery();
                TEAccount account = null;
                while (results.next()) {
                    if (account == null) {
                        account = new TEAccount(
                            UUID.fromString(results.getString("id")),
                            results.getString("display_name"),
                            new HashMap<>()
                        );
                    }

                    TECurrency currency = new TECurrency(
                        results.getInt("currency_id"),
                        results.getString("name_singular"),
                        results.getString("name_plural"),
                        results.getString("symbol"),
                        results.getInt("num_fraction_digits"),
                        results.getBoolean("is_default")
                    );
                    BigDecimal balance = results.getBigDecimal("balance");

                    account.balances.put(currency, balance);
                }

                return account;
            }
        } catch (SQLException e) {
            logger.error(String.format("Error getting account (Query: %s, Parameters: %s)", query, uuid));
        }

        return null;
    }

    public Balance getBalance(UUID userId, int currencyId) {
        String query = "SELECT * FROM te_balance WHERE user_id = ? AND currency_id = ?";

        try (Connection conn = database.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, userId.toString());
                stmt.setInt(2, currencyId);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new Balance(
                        UUID.fromString(rs.getString("user_id")),
                        rs.getInt("currency_id"),
                        rs.getBigDecimal("balance")
                    );
                }
            }
        } catch (SQLException e) {
            logger.error(String.format("Error getting balance from database (Query: %s, Parameters: %s, %s)", query, userId, currencyId));
        }

        return null;
    }

    public List<Balance> getBalances(UUID userId) {
        String query = "SELECT user_id, currency_id, balance \n"
            + "FROM te_balance\n"
            + "WHERE user_id = ?";

        List<Balance> balances = new ArrayList<>();
        try (Connection conn = database.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, userId.toString());

                ResultSet results = stmt.executeQuery();
                while (results.next()) {
                    Balance balance = new Balance(
                        UUID.fromString(results.getString("user_id")),
                        results.getInt("currency_id"),
                        results.getBigDecimal("balance")
                    );
                    balances.add(balance);
                }

                return balances;
            }
        } catch (SQLException e) {
            logger.error(String.format("Error getting balance from database (Query: %s, Parameters: %s)", query, userId));
        }

        return balances;
    }

    public Balance setBalance(Balance balance) {
        String query = "UPDATE te_balance SET balance = ? WHERE user_id = ? AND currency_id = ?";

        try (Connection conn = database.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setBigDecimal(1, balance.getBalance());
                stmt.setString(2, balance.getUserId().toString());
                stmt.setInt(3, balance.getCurrencyId());
                stmt.executeUpdate();

                return balance;
            }
        } catch (SQLException e) {
            logger.error(
                String.format(
                    "Error setting balance (Query: %s, Parameters: %.0f, %s, %s)",
                    query,
                    balance.getBalance(),
                    balance.getUserId().toString(),
                    balance.getCurrencyId()
                )
            );
        }

        return null;
    }

    public boolean setTransferBalances(Balance updatedFromBalance, Balance updatedToBalance) {
        String query = "UPDATE te_balance SET balance = ? WHERE user_id = ? AND currency_id = ?";

        try (Connection conn = database.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setBigDecimal(1, updatedFromBalance.getBalance());
                stmt.setString(2, updatedFromBalance.getUserId().toString());
                stmt.setInt(3, updatedFromBalance.getCurrencyId());
                stmt.executeUpdate();

                stmt.setBigDecimal(1, updatedToBalance.getBalance());
                stmt.setString(2, updatedToBalance.getUserId().toString());
                stmt.setInt(3, updatedToBalance.getCurrencyId());
                stmt.executeUpdate();
            } catch (SQLException e) {
                conn.rollback();
                logger.error(
                    String.format(
                        "Error on transfer (Query: %s, Parameters: %s, %.0f, %s, %s, %.0f, %s)",
                        query,
                        updatedFromBalance.getUserId().toString(),
                        updatedFromBalance.getBalance(),
                        updatedFromBalance.getCurrencyId(),
                        updatedToBalance.getUserId().toString(),
                        updatedToBalance.getBalance(),
                        updatedToBalance.getCurrencyId()
                    )
                );

                return false;
            }

            conn.commit();

            return true;
        } catch (SQLException e) {
            logger.error(
                String.format(
                    "Error on transfer (Query: %s, Parameters: %s, %.0f, %s, %s, %.0f, %s)",
                    query,
                    updatedFromBalance.getUserId().toString(),
                    updatedFromBalance.getBalance(),
                    updatedFromBalance.getCurrencyId(),
                    updatedToBalance.getUserId().toString(),
                    updatedToBalance.getBalance(),
                    updatedToBalance.getCurrencyId()
                )
            );
        }

        return false;
    }
}
