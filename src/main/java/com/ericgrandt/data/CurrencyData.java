package com.ericgrandt.data;

import com.ericgrandt.domain.TECurrency;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.service.economy.Currency;

public class CurrencyData {
    private final Logger logger = LogManager.getLogger("TotalEconomy");
    private final Database database;

    public CurrencyData(Database database) {
        this.database = database;
    }

    public Currency getDefaultCurrency() {
        try (Connection conn = database.getConnection()) {
            String query = "SELECT * FROM te_currency WHERE is_default = true LIMIT 1";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet results = stmt.executeQuery();
                results.next();

                return new TECurrency(
                    results.getInt("id"),
                    results.getString("name_singular"),
                    results.getString("name_plural"),
                    results.getString("symbol"),
                    true
                );
            }
        } catch (SQLException e) {
            logger.error("Error getting default currency from database");
        }

        return null;
    }

    public Currency getCurrency(String currencyName) {
        String query = "SELECT * FROM te_currency WHERE name_singular = ? LIMIT 1";

        try (Connection conn = database.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, currencyName);

                ResultSet results = stmt.executeQuery();

                if (results.next()) {
                    return new TECurrency(
                        results.getInt("id"),
                        results.getString("name_singular"),
                        results.getString("name_plural"),
                        results.getString("symbol"),
                        results.getBoolean("is_default")
                    );
                }
            }
        } catch (SQLException e) {
            logger.error(String.format("Error getting currency from database (Query: %s, Parameters: %s)", query, currencyName));
        }

        return null;
    }

    public Set<Currency> getCurrencies() {
        try (Connection conn = database.getConnection()) {
            String query = "SELECT * FROM te_currency";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet results = stmt.executeQuery();

                Set<Currency> currencies = new HashSet<>();
                while (results.next()) {
                    Currency currency = new TECurrency(
                        results.getInt("id"),
                        results.getString("name_singular"),
                        results.getString("name_plural"),
                        results.getString("symbol"),
                        results.getBoolean("is_default")
                    );

                    currencies.add(currency);
                }

                return currencies;
            }
        } catch (SQLException e) {
            logger.error("Error getting currencies from database");
        }

        return new HashSet<>();
    }
}
