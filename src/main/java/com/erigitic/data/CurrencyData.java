package com.erigitic.data;

import com.erigitic.TotalEconomy;
import com.erigitic.economy.TECurrency;
import org.slf4j.Logger;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import java.sql.*;
import java.util.*;

public class CurrencyData {
    private final TotalEconomy plugin;
    private final Logger logger;
    private final Database database;

    public CurrencyData(Database database) {
        this.plugin = TotalEconomy.getPlugin();
        this.logger = plugin.getLogger();
        this.database = database;
    }

    public Currency getDefaultCurrency() {
        try (Connection conn = database.getConnection()) {
            String query = "SELECT nameSingular, namePlural, symbol FROM currency WHERE isDefault = true LIMIT 1";

            try(PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet results = stmt.executeQuery();
                results.next();

                Currency defaultCurrency = new TECurrency(
                    Text.of(results.getString("nameSingular")),
                    Text.of(results.getString("namePlural")),
                    Text.of(results.getString("symbol").charAt(0)),
                    true
                );

                return defaultCurrency;
            }
        } catch (SQLException e) {
            logger.error("Error getting default currency from database");
        }

        return null;
    }

    public Currency getCurrency(String identifier) {
        String query = "SELECT nameSingular, namePlural, symbol, isDefault FROM currency WHERE nameSingular = ? LIMIT 1";

        try (Connection conn = database.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, identifier);

                ResultSet results = stmt.executeQuery();

                if (results.next()) {
                    Currency currency = new TECurrency(
                        Text.of(results.getString("nameSingular")),
                        Text.of(results.getString("namePlural")),
                        Text.of(results.getString("symbol").charAt(0)),
                        results.getBoolean("isDefault")
                    );

                    return currency;
                }
            }
        } catch (SQLException e) {
            logger.error(String.format("Error getting currency from database (Query: %s, Parameters: %s)", query, identifier));
        }

        return null;
    }

    public Set<Currency> getCurrencies() {
        try (Connection conn = database.getConnection()) {
            String query = "SELECT * FROM currency";

            try(PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet results = stmt.executeQuery();

                Set<Currency> currencies = new HashSet<>();
                while (results.next()) {
                    Currency currency = new TECurrency(
                        Text.of(results.getString("nameSingular")),
                        Text.of(results.getString("namePlural")),
                        Text.of(results.getString("symbol").charAt(0)),
                        results.getBoolean("isDefault")
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
