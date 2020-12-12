package com.erigitic;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class TestUtils {
    public static Connection createTestConnection() {
        URL testPropsPath = TestUtils.class.getClassLoader().getResource("test.properties");
        Properties testProps = new Properties();

        try {
            testProps.load(new FileInputStream(testPropsPath.getFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String connectionString = testProps.getProperty("connectionString");
        String user = testProps.getProperty("user");
        String password = testProps.getProperty("password");

        try {
            return DriverManager.getConnection(String.format("%s?user=%s&password=%s", connectionString, user, password));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void seedDb() {
        try (Connection conn = TestUtils.createTestConnection()) {
            String insertDollarCurrency = "INSERT INTO te_currency\n" +
                "VALUES(0, 'Dollar', 'Dollars', '$', true, true)";
            String insertEuroCurrency = "INSERT INTO te_currency\n" +
                "VALUES(1, 'Euro', 'Euros', '\u20ac', false, true)";

            Statement statement = conn.createStatement();
            statement.execute(insertDollarCurrency);
            statement.execute(insertEuroCurrency);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void resetDb() {
        try (Connection conn = TestUtils.createTestConnection()) {
            String truncateUsers = "DELETE FROM te_user";
            String truncateBalances = "DELETE FROM te_balance";
            String truncateCurrencies = "DELETE FROM te_currency";

            Statement statement = conn.createStatement();
            statement.execute(truncateUsers);
            statement.execute(truncateBalances);
            statement.execute(truncateCurrencies);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
