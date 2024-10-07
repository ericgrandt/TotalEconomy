package com.ericgrandt.totaleconomy.common.data;

import com.ericgrandt.totaleconomy.common.domain.Currency;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CurrencyData {
    private final Database database;

    public CurrencyData(Database database) {
        this.database = database;
    }

    public Currency getDefaultCurrency() throws SQLException {
        String query = "SELECT * FROM te_currency WHERE is_default IS TRUE LIMIT 1";

        try (
            Connection conn = database.getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Currency(
                        rs.getInt("id"),
                        rs.getString("name_singular"),
                        rs.getString("name_plural"),
                        rs.getString("symbol"),
                        rs.getInt("num_fraction_digits"),
                        rs.getBoolean("is_default")
                    );
                }
            }
        }

        return null;
    }
}
