package com.ericgrandt.totaleconomy.data;

import com.ericgrandt.totaleconomy.exception.EntityNotFoundException;
import com.ericgrandt.totaleconomy.model.TECurrency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CurrencyData {
    public TECurrency getDefaultCurrency(Connection conn) throws SQLException {
        var query = "SELECT code, name, plural_name, symbol, fractional_digits, is_default FROM te_currency WHERE is_default IS TRUE LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new TECurrency(
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("plural_name"),
                        rs.getString("symbol"),
                        rs.getInt("fractional_digits"),
                        rs.getBoolean("is_default")
                    );
                }
            }
        }

        throw new EntityNotFoundException("Default currency not found");
    }

    public TECurrency getCurrency(Connection conn, String currencyCode) throws SQLException {
        var query = "SELECT code, name, plural_name, symbol, fractional_digits, is_default FROM te_currency WHERE code = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, currencyCode);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new TECurrency(
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("plural_name"),
                        rs.getString("symbol"),
                        rs.getInt("fractional_digits"),
                        rs.getBoolean("is_default")
                    );
                }
            }
        }

        throw new EntityNotFoundException("Currency not found");
    }
}
