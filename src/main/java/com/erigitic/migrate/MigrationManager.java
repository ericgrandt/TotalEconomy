package com.erigitic.migrate;

import com.erigitic.sql.TESqlManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("squid:S2095")
public class MigrationManager {

    private static final int LATEST_SCHEMA_VERSION = 2;

    private static final Logger logger = LoggerFactory.getLogger("totaleconomy");

    private final TESqlManager sqlManager;

    public MigrationManager(TESqlManager sqlManager) {
        this.sqlManager = sqlManager;
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
