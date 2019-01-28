package com.erigitic.migrate;

import com.erigitic.except.TEMigrationException;
import com.erigitic.migrate.exec.Migrator;
import com.erigitic.sql.TESqlManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("squid:S2095")
public class MigrationManager {

    private static final int LATEST_SCHEMA_VERSION = 2;

    // Better than Getters for loggers?
    // Worse?
    // ID instead of class? Or opposite?
    private static final Logger logger = LoggerFactory.getLogger("totaleconomy");

    private static final List<Migrator> fileMigrators = new LinkedList<>();
    private static final List<Migrator> dbMigrators = new LinkedList<>();

    private final TESqlManager sqlManager;

    public MigrationManager(TESqlManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    public void migrate() {
        if (!sqlManager.isEnabled()) {
            fileMigrate();
        } else {
            dbMigrate();
        }
    }

    private void fileMigrate() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    private void dbMigrate() {
        try (Connection migrationConnection = sqlManager.getDataSource().getConnection()) {
            int currentVersion = detectVersion(migrationConnection);

            if (currentVersion == LATEST_SCHEMA_VERSION) {
                logger.info("Version fits. Nothing to do.");

            } else if (currentVersion > LATEST_SCHEMA_VERSION) {
                throw new TEMigrationException("Current schema is ahead of the supported data format! Unable to migrate!");
            } else {
                runMigrators(dbMigrators);
            }
        } catch (SQLException e) {
            throw new TEMigrationException("A fatal error occurred trying to run db migrations!", e);
        }
    }

    private void runMigrators(List<Migrator> migrators) {

    }

    private int detectVersion(Connection connection) {
        // Check for legacy schema first.
        if (isLegacyDB(connection)) {
            return 0;
        }

        String tableName = sqlManager.getTablePrefix() + "meta";
        String query = "SELECT value FROM ? WHERE ident = 'schema_version'";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, tableName);
            ResultSet resultSet = statement.executeQuery();
            String value = resultSet.getString(1);
            return Integer.parseInt(value);

        } catch (SQLException e) {
            logger.error("Failed to detect schema version! SQL failure.", e);
            return -1;
        } catch (NumberFormatException e) {
            logger.error("Failed to determine schema version! NumberFormat failure.", e);
            return LATEST_SCHEMA_VERSION;
        }
    }

    private boolean isLegacyDB(Connection connection) {
        String query = "SELECT DISTINCT TABLE_NAME FROM information_schema.COLUMNS WHERE SCHEMA_NAME = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, sqlManager.getDatabaseName());

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String tableName = resultSet.getString(1);
                if ("virtual_accounts".equals(tableName)) {
                    return true;
                }
            }

        } catch (SQLException e) {
            logger.error("Could not determine legacy-status. Assuming 'false'.", e);
        }

        return false;
    }
}
