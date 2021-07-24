package com.ericgrandt.data;

import com.ericgrandt.TotalEconomy;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.asset.Asset;

public class Database {
    private final TotalEconomy plugin;
    private final Logger logger;

    public Database(Logger logger, TotalEconomy plugin) {
        this.plugin = plugin;
        this.logger = logger;
    }

    public Connection getConnection() throws SQLException {
        String connectionString = Objects.requireNonNull(plugin.getDefaultConfiguration().get()).getConnectionString();
        return DriverManager.getConnection(connectionString);
    }

    public void setup() {
        String databaseProvider = getDatabaseProvider();
        if (!databaseProvider.equals("mysql") && !databaseProvider.equals("h2")) {
            logger.error("Could not find SQL file");
            return;
        }

        runSqlScript();
    }

    private String getDatabaseProvider() {
        String connectionString = Objects.requireNonNull(plugin.getDefaultConfiguration().get()).getConnectionString();
        return connectionString.split("jdbc:")[1].split(":")[0];
    }

    private void runSqlScript() {
        Asset sqlScript = plugin.getMysqlSchema();

        try (Connection conn = getConnection();
             InputStreamReader reader = new InputStreamReader(sqlScript.url().openStream())
        ) {
            logger.info("Successfully connected");

            ScriptRunner runner = new ScriptRunner(conn);
            runner.setLogWriter(null);
            runner.runScript(reader);

            logger.info("Database successfully setup");
        } catch (SQLException e) {
            logger.error("Unable to connect to the database: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("Could not read SQL file");
        }
    }
}
