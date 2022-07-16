package com.ericgrandt.data;

import com.ericgrandt.TotalEconomy;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.logging.log4j.Logger;
import org.spongepowered.plugin.PluginContainer;

public class Database {
    private final PluginContainer pluginContainer;
    private final Logger logger;
    private final TotalEconomy plugin;

    public Database(Logger logger, PluginContainer pluginContainer) {
        this.pluginContainer = pluginContainer;
        this.logger = logger;
        this.plugin = (TotalEconomy) pluginContainer.instance();
    }

    public void setup() {
        String databaseProvider = getDatabaseProvider();
        if (!databaseProvider.equals("mysql") && !databaseProvider.equals("h2")) {
            logger.error("Could not find SQL file");
            return;
        }

        runSqlScript();
    }

    private void runSqlScript() {
        try (
            Connection conn = getConnection();
            InputStreamReader reader = new InputStreamReader(getSchemaSqlInputStream())
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

    public Connection getConnection() throws SQLException {
        String connectionString = Objects.requireNonNull(plugin.getDefaultConfiguration().get()).getConnectionString();
        return DriverManager.getConnection(connectionString);
    }

    private String getDatabaseProvider() {
        String connectionString = Objects.requireNonNull(plugin.getDefaultConfiguration().get()).getConnectionString();
        return connectionString.split("jdbc:")[1].split(":")[0];
    }

    private InputStream getSchemaSqlInputStream() throws FileNotFoundException {
        URI schemaSqlURI = URI.create("schema/mysql.sql");
        return pluginContainer.openResource(schemaSqlURI).orElseThrow(FileNotFoundException::new);
    }
}
