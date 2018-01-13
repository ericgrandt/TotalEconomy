package com.erigitic.sql;

import com.erigitic.jobs.JobManager;
import com.erigitic.jobs.TEJob;
import com.erigitic.main.TotalEconomy;
import com.erigitic.sql.migration.MigrationManager;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL manager
 *
 * @author Erigitic
 * @author MarkL4YG
 */
public class SqlManager {

    public final Integer SCHEMA_VERSION = 1;

    private Logger logger;
    private DataSource dataSource;

    private String databaseName = "totaleconomy";
    private Integer autoMigrateFrom = -2;

    public SqlManager(Logger logger) {
        this.logger = logger;
    }

    public void initDataSource(String url, String username, String password) {

        try {
            SqlService sqlService = Sponge.getServiceManager().provideUnchecked(SqlService.class);
            dataSource = sqlService.getDataSource("jdbc:" + url + "?user=" + username + "&password=" + password);

            // Note: This pattern does support IPv6
            // Reference https://regex101.com/r/YmyHYA/1 (Link may expire at some point)
            Pattern dbNamePattern = Pattern.compile("\\w+://.+(:\\d+)?/(\\w+)");
            Matcher m = dbNamePattern.matcher(url);
            if (m.matches()) {
                databaseName = m.group(2);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create dataSource for databae connection!", e);
        }
    }

    public void initDatabase(TotalEconomy totalEconomy) {
        try {

            // Database migration checks.
            // This property allows to skip the check and thus all automatic database migrations.
            if (!"true".equals(System.getProperty("totaleconomy.skipDBMigrateCheck", null))) {
                Connection connection = dataSource.getConnection();
                DatabaseMetaData metaData = connection.getMetaData();
                try (ResultSet result = metaData.getTables(null, null, "te_meta", null)) {

                    // Does the table exist?
                    // No? Migrate from first schema!
                    if (!result.next()) {

                        // Check if we need to perform flat file migration.
                        String query = "SELECT COUNT(DISTINCT `table_name`) FROM `information_schema`.`columns` WHERE `table_schema` = '" + databaseName + "'";

                        try (Statement statement = connection.createStatement()) {
                            statement.executeQuery(query);
                            ResultSet tableCountResult = statement.getResultSet();
                            if (tableCountResult.isBeforeFirst()) {
                                tableCountResult.next();
                            }
                            Integer tables = tableCountResult.getInt(1);
                            autoMigrateFrom = tables < 1 ? -1 : 0;
                        }

                        query = "CREATE TABLE IF NOT EXISTS `te_meta` ("
                                + "ident VARCHAR(60) NOT NULL PRIMARY KEY,"
                                + "value VARCHAR(60) DEFAULT NULL"
                                + ") COMMENT='Table for versioning an other internal stuff'";
                        createTable(connection, query);

                        // Insert our schema version
                        query = "INSERT INTO te_meta (ident, value) VALUES ('schema_version', '1')";
                        try (Statement statement = connection.createStatement()) {

                            if (statement.executeUpdate(query) != 1) {
                                throw new SQLException("Failed to set schema version");
                            }
                        }
                    } else {
                        String query = "SELECT value FROM te_meta WHERE ident = 'schema_version'";

                        try (Statement statement = connection.createStatement()) {
                            statement.executeQuery(query);
                            ResultSet versionResult = statement.getResultSet();

                            if (!versionResult.next()) {
                                throw new SQLException("FATAL: Key 'schema_version' is missing in table te_meta!");
                            }
                            Integer schemaVersion = Integer.parseInt(versionResult.getString("value"));

                            if (schemaVersion < SCHEMA_VERSION) {
                                autoMigrateFrom = schemaVersion;
                            } else if (schemaVersion > SCHEMA_VERSION) {
                                throw new RuntimeException("Cannot automatically migrate downwards!");
                            }
                        } catch (SQLException | NumberFormatException e) {
                            throw new RuntimeException("Failed to check for table migrations!", e);
                        }
                    }
                }
                connection.close();
            } else {
                logger.warn("MIGRATE CHECK SKIPPED. This can be dangerous when you're updating TotalEconomy.");
            }

            // Run table creation only if there can't be any tables that would need migration
            if (autoMigrateFrom < 0) {
                createTables();
            }

            // Do we need to run the migration
            if (autoMigrateFrom != -2) {
                new MigrationManager(totalEconomy, logger, autoMigrateFrom).run();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to set up database and tables!", e);
        }

    }

    /**
     * Creates the necessary database tables
     *
     * @throws SQLException Upon error
     */
    public void createTables() throws SQLException {
        Connection connection = dataSource.getConnection();
        // Create tables if they don't exist.
        String query = "CREATE TABLE IF NOT EXISTS `accounts` ("
                       + "uid         VARCHAR(60)  NOT NULL PRIMARY KEY,"
                       + "displayname VARCHAR(60)  DEFAULT NULL,"
                       + "job         VARCHAR(60)  DEFAULT NULL"
                       + ") COMMENT='Main accounts table';";
        createTable(connection, query);

        query = "CREATE TABLE IF NOT EXISTS `accounts_options` ("
                + "id    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,"
                + "uid   VARCHAR(60)  NOT NULL,"
                + "ident VARCHAR(60)  NOT NULL,"
                + "value VARCHAR(255) DEFAULT NULL,"
                + "FOREIGN KEY (uid) REFERENCES accounts(uid) ON UPDATE CASCADE ON DELETE CASCADE"
                + ") COMMENT='User account options';";
        createTable(connection, query);

        query = "CREATE TABLE IF NOT EXISTS `currencies` ("
                + "currency VARCHAR(60) NOT NULL PRIMARY KEY"
                + ") COMMENT='DATABASE REFERENCE | All currencies | Automatically updated on restart';";
        createTable(connection, query);

        query = "CREATE TABLE IF NOT EXISTS `balances` ("
                + "id       INT UNSIGNED  AUTO_INCREMENT PRIMARY KEY,"
                + "uid      VARCHAR(60)   NOT NULL,"
                + "currency VARCHAR(60)   NOT NULL,"
                + "balance  DECIMAL(19,2) DEFAULT 0,"
                + "FOREIGN KEY (uid) REFERENCES accounts(uid) ON DELETE CASCADE ON UPDATE CASCADE,"
                + "FOREIGN KEY (currency) REFERENCES currencies(currency) ON DELETE CASCADE ON UPDATE CASCADE,"
                + "CONSTRAINT UNIQUE (uid, currency)"
                + ") COMMENT='All balances by account and currency';";
        createTable(connection, query);

        query = "CREATE TABLE IF NOT EXISTS `jobs` ("
                + "uid VARCHAR(60) NOT NULL PRIMARY KEY"
                + ") COMMENT='DATABASE REFERENCE | All jobs | Automatically updated on restart';";
        createTable(connection, query);

        // TODO: Job specification in DB?

        query = "CREATE TABLE IF NOT EXISTS `jobs_progress` ("
                + "id         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,"
                + "uid        VARCHAR(60)  NOT NULL,"
                + "job        VARCHAR(60)  NOT NULL,"
                + "level      INT UNSIGNED DEFAULT 0,"
                + "experience INT UNSIGNED DEFAULT 0,"
                + "FOREIGN KEY (uid) REFERENCES accounts(uid) ON DELETE CASCADE ON UPDATE CASCADE,"
                + "FOREIGN KEY (job) REFERENCES jobs(uid) ON DELETE CASCADE ON UPDATE CASCADE,"
                + "CONSTRAINT UNIQUE (uid, job)"
                + ") COMMENT='The job progress table';";
        createTable(connection, query);
        connection.close();

        // Just a trigger for the IntelliJ statement inspections :)
        // (It won't run sql inspections on the query strings above otherwise)
        if (false) {
            dataSource.getConnection().createStatement().executeUpdate(query);
        }
    }

    private void createTable(Connection connection, String statement) throws SQLException {
        try (Statement sqlStatement = connection.createStatement()) {
            if (sqlStatement.executeUpdate(statement) != 0) {
                throw new SQLException("Unexpected return value for query: " + statement);
            }
        }
    }

    public void postInitDatabase(JobManager jobManager) {


        Connection connection;
        try {
            connection = getDataSource().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to post-initialize database", e);
        }

        // Update currencies
        logger.info("Updating currency reference in database");
        EconomyService econService = Sponge.getServiceManager().provideUnchecked(EconomyService.class);
        Set<Currency> currencies = econService.getCurrencies();

        StringBuilder deleteQuery = new StringBuilder("DELETE FROM currencies WHERE NOT currency IN (");
        StringBuilder insertQuery = new StringBuilder("INSERT IGNORE INTO currencies (currency) VALUES ");
        int pos = 0;
        for (Currency currency : currencies) {
            deleteQuery.append("'").append(currency.getName().toLowerCase()).append("'");
            insertQuery.append("('").append(currency.getName().toLowerCase()).append("')");

            if (++pos != currencies.size()) {
                deleteQuery.append(",");
                insertQuery.append(",");
            } else {
                deleteQuery.append(")");
            }
        }

        try {
            try (PreparedStatement statement = connection.prepareStatement(deleteQuery.toString())) {
                statement.executeUpdate();
            }

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(insertQuery.toString());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update currencies table!", e);
        }

        // Update jobs
        logger.info("Updating job reference in database");
        if (jobManager != null) {
            Map<String, TEJob> jobs = jobManager.getJobs();
            deleteQuery = new StringBuilder("DELETE FROM jobs WHERE NOT uid IN (");
            insertQuery = new StringBuilder("INSERT IGNORE INTO jobs (uid) VALUES ");
            pos = 0;

            for (Map.Entry<String, TEJob> entry : jobs.entrySet()) {
                deleteQuery.append("'").append(entry.getKey()).append("'");
                insertQuery.append("('").append(entry.getKey()).append("')");

                if (++pos != jobs.size()) {
                    deleteQuery.append(",");
                    insertQuery.append(",");
                } else {
                    deleteQuery.append(")");
                }
            }

            try {

                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(deleteQuery.toString());
                }

                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(insertQuery.toString());
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to update jobs table!", e);
            }
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public String getDatabaseName() {
        return databaseName;
    }
}
