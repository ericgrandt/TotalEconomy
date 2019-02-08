package com.erigitic.migrate.exec;

import com.erigitic.except.TEMigrationException;
import com.erigitic.migrate.util.TableBuilder;
import com.erigitic.sql.TESqlManager;
import org.intellij.lang.annotations.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Utility layer for database migrations.
 */
public abstract class AbstractDBMigrator extends Migrator {

    private static final Logger logger = LoggerFactory.getLogger(AbstractDBMigrator.class);

    private Connection connection;

    protected PreparedStatement prepare(@Language("MySQL") String query) throws SQLException {
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
        @Language("MySQL")
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
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    protected void dropTable(String from, boolean force) {
        logger.warn("Dropping table: {}", from);
        String query = "DROP TABLE ?";
        try (PreparedStatement statement = prepare(query)) {
            if (force) {
                // Save the current FK check state so we don't mutate state outside of this method.
                statement.executeQuery("SET @FK_CHECKSTATE = @@FOREIGN_KEY_CHECKS");
                statement.executeQuery("SET FOREIGN_KEY_CHECKS=0");
            }

            statement.setString(1, from);
            statement.execute();

            if (force) {
                statement.executeQuery("SET FOREIGN_KEY_CHECKS = @FK_CHECKSTATE");
            }
        } catch (SQLException e) {
            throw new TEMigrationException("Failed to drop table: " + from, e);
        }
    }

    protected boolean tableHasColumn(String table, String column) {

        @Language("MySQL")
        String query = "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND COLUMN_NAME = ?";
        try (PreparedStatement statement = prepare(query)) {
            statement.setString(1, getSqlManager().getDatabaseName());
            statement.setString(2, table);
            statement.setString(3, column);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.isBeforeFirst() && "1".equals(resultSet.getString(1));

        } catch (SQLException e) {
            throw new TEMigrationException("Failed to determine if table has column: " + table + " " + column);
        }
    }

    protected TESqlManager getSqlManager() {
        return getPlugin().getTESqlManager();
    }

    protected Connection getConnection() {
        return connection;
    }
}
