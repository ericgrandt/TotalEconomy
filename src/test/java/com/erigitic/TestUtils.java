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

    public static void resetDb() {
        try (Connection conn = TestUtils.createTestConnection()) {
            String truncateUsers = "DELETE FROM te_user";
            String truncateBalances = "DELETE FROM te_balance";

            Statement statement = conn.createStatement();
            statement.execute(truncateUsers);
            statement.execute(truncateBalances);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
