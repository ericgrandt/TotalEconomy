package com.ericgrandt;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.ibatis.jdbc.ScriptRunner;

public class TestUtils {
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    static {
        config.setJdbcUrl("jdbc:h2:mem:totaleconomy");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
        setupDb();
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static void setupDb() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("schema.sql");
        try (Connection conn = TestUtils.getConnection();
             InputStreamReader reader = new InputStreamReader(is)
        ) {
            ScriptRunner runner = new ScriptRunner(conn);
            runner.runScript(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void seedCurrencies() {
        try (Connection conn = TestUtils.getConnection()) {
            String insertDollarCurrency = "INSERT INTO te_currency\n"
                + "VALUES(1, 'Dollar', 'Dollars', '$', 0, true)";
            String insertEuroCurrency = "INSERT INTO te_currency\n"
                + "VALUES(2, 'Euro', 'Euros', 'E', 0, false)";

            Statement statement = conn.createStatement();
            statement.execute(insertDollarCurrency);
            statement.execute(insertEuroCurrency);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void seedAccounts() {
        try (Connection conn = TestUtils.getConnection()) {
            String insertAccount = "INSERT INTO te_account\n"
                + "VALUES('62694fb0-07cc-4396-8d63-4f70646d75f0', '2022-01-01 00:00:00');";
            String insertBalance = "INSERT INTO te_balance\n"
                + "VALUES('ab661384-11f5-41e1-a5e6-6fa93305d4d1', '62694fb0-07cc-4396-8d63-4f70646d75f0', 1, 50)";
            String insertAccount2 = "INSERT INTO te_account\n"
                + "VALUES('551fe9be-f77f-4bcb-81db-548db6e77aea', '2022-01-02 00:00:00');";
            String insertBalance2 = "INSERT INTO te_balance\n"
                + "VALUES('a766cedf-f53e-450d-804a-4f292357938f', '551fe9be-f77f-4bcb-81db-548db6e77aea', 1, 100)";

            Statement statement = conn.createStatement();
            statement.execute(insertAccount);
            statement.execute(insertBalance);
            statement.execute(insertAccount2);
            statement.execute(insertBalance2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void seedVirtualAccounts() {
        try (Connection conn = TestUtils.getConnection()) {
            String insertVirtualAccount1 = "INSERT INTO te_virtual_account\n"
                + "VALUES('af65cab9-7ef9-4108-87f9-4ee66a289bfa', 'virtualAccount1', '2022-01-01 00:00:00');";
            String insertVirtualAccount2 = "INSERT INTO te_virtual_account\n"
                + "VALUES('7cf27525-6491-4b3e-9208-ce232adf7c87', 'virtualAccount2', '2022-01-02 00:00:00');";

            Statement statement = conn.createStatement();
            statement.execute(insertVirtualAccount1);
            statement.execute(insertVirtualAccount2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void seedDefaultBalances() {
        try (Connection conn = TestUtils.getConnection()) {
            String insertDefaultBalance1 = "INSERT INTO te_default_balance\n"
                + "VALUES('05231a59-b6fa-4d57-8450-5bd07f148a98', 1, 100.50);";
            String insertDefaultBalance2 = "INSERT INTO te_default_balance\n"
                + "VALUES('ce369d91-b70e-47a4-84d1-6f47eec3b0a8', 2, 50);";

            Statement statement = conn.createStatement();
            statement.execute(insertDefaultBalance1);
            statement.execute(insertDefaultBalance2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void resetDb() {
        try (Connection conn = TestUtils.getConnection()) {
            String deleteUsers = "DELETE FROM te_account";
            String deleteBalances = "DELETE FROM te_balance";
            String deleteCurrencies = "DELETE FROM te_currency";

            Statement statement = conn.createStatement();
            statement.execute(deleteUsers);
            statement.execute(deleteBalances);
            statement.execute(deleteCurrencies);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
