package com.ericgrandt.data;

import com.ericgrandt.domain.TECurrency;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.service.economy.Currency;

public class CurrencyData {
    private final Logger logger;
    private final Database database;

    public CurrencyData(Logger logger, Database database) {
        this.logger = logger;
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
                    results.getInt("num_fraction_digits"),
                    true
                );
            }
        } catch (SQLException e) {
            logger.error("Error getting default currency from database");
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
                        results.getInt("num_fraction_digits"),
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
