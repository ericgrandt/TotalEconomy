package com.erigitic.migrate.exec;

import com.erigitic.except.TEMigrationException;
import com.erigitic.migrate.util.TableBuilder;
import com.erigitic.sql.TESqlManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Migration layer for database migration.
 * Basically a utility class.
 */
public abstract class AbstractDBMigrator implements Migrator {

    private static final Logger logger = LoggerFactory.getLogger(AbstractDBMigrator.class);

    private TESqlManager sqlManager;
    private Connection connection;

    protected PreparedStatement prepare(String query) throws SQLException {
        return getConnection().prepareStatement(query);
    }

    protected void foreignKeyChecks(boolean enabled) {
        try (PreparedStatement foreignKeyChecks = prepare("SET FOREIGN_KEY_CHECKS = ?")) {
            foreignKeyChecks.setBoolean(1, enabled);
            foreignKeyChecks.execute();
        } catch (SQLException e) {
            throw new TEMigrationException("Failed to change foreign key checks!", e);
        }
    }

    protected String getEffectiveTableName(String normalName) {
        return getSqlManager().getTablePrefix()
                .map(prefix -> prefix + normalName)
                .orElse(normalName);
    }

    protected void renameTable(String from, String to) {
        logger.warn("Renaming table {} to {}", from, to);
        //language=MySQL
        String query = "RENAME TABLE ? TO ?";
        try (PreparedStatement statement = prepare(query)) {
            statement.setString(1, from);
            statement.setString(2, to);
            statement.execute();

        } catch (SQLException e) {
            throw new TEMigrationException("Failed to rename table: " + from + " to " + to, e);
        }
    }

    protected void createTable(String name, TableBuilder builder) {

    }

    protected void dropTable(String from, boolean force) {
        logger.warn("Dropping table: {}", from);
        String query = "DROP TABLE ?";
        try (PreparedStatement statement = prepare(query)) {
            if (force) {
                statement.executeQuery("SET FOREIGN_KEY_CHECKS=0");
            }

            statement.setString(1, from);
            statement.execute();

            if (force) {
                statement.executeQuery("SET FOREIGN_KEY_CHECKS=1");
            }
        } catch (SQLException e) {
            throw new TEMigrationException("Failed to drop table: " + from, e);
        }
    }

    public TESqlManager getSqlManager() {
        return sqlManager;
    }

    protected Connection getConnection() {
        return connection;
    }

    @Override
    public void setSqlManager(TESqlManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    @Override
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
