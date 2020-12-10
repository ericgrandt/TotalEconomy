package com.erigitic;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class TestUtils {
    public static String getConnectionString() {
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

        return String.format("%s?user=%s&password=%s", connectionString, user, password);
    }

    public static Connection createTestConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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
}
