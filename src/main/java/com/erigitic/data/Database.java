package com.erigitic.data;

import com.erigitic.TotalEconomy;
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
    private final String connectionString;
    private final String databaseProvider;

    public Database() {
        plugin = TotalEconomy.getPlugin();
        logger = plugin.getLogger();
        connectionString = Objects.requireNonNull(plugin.getDefaultConfiguration().get()).getConnectionString();
        databaseProvider = getDatabaseProvider();
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }

    public void setup() {
        if (!databaseProvider.equals("mysql")) {
            logger.error("Could not find SQL file");
            return;
        }

        runSqlScript(plugin.getMysqlSchema());
    }

    private String getDatabaseProvider() {
        return connectionString.split("jdbc:")[1].split(":")[0];
    }

    private void runSqlScript(Asset sqlScript) {
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
