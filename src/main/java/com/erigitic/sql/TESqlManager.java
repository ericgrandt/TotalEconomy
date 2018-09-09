package com.erigitic.sql;

import com.erigitic.except.TEConnectionException;
import com.erigitic.main.TotalEconomy;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manages and maintains database connection established using JDBC urls.
 * Provides separate connections for account and job queries for simultaneous requests.
 */
public class TESqlManager implements AutoCloseable {

    private static final Pattern JDBC_DB_NAME = Pattern.compile("\\w+://.+(?::\\d+)?/(\\w+)\\??");
    private static final Pattern JDBC_DB_USERNAME = Pattern.compile("\\?.*(user=.+)(?:&.*)");
    private static final Pattern JDBC_DB_PASSWORD = Pattern.compile("\\?.*(password=.+)(?:&.*)");
    private static final Pattern JDBC_DB_PREFIX = Pattern.compile("\\?.*(prefix=.+)(?:&.*)");

    private Logger logger;
    private TotalEconomy totalEconomy;
    private DataSource dataSource;
    private Connection accountsConnection;
    private Connection jobsConnection;
    private Connection commandConnection;
    private String tablePrefix;

    public TESqlManager(TotalEconomy totalEconomy, Logger logger) {
        this.totalEconomy = totalEconomy;
        this.logger = logger;
    }

    /**
     * Initializes the connection with a database URL from the provided {@link TotalEconomy} instance.
     */
    public void initialize() {
        initialize(totalEconomy.getDatabaseUrl());
    }

    /**
     * Initializes the SQLManager with a JDBC connection url.
     */
    public void initialize(String jdbcUrl) {
        if (jdbcUrl == null) {
            throw new IllegalArgumentException("Database URL may not be null!");
        }

        if (dataSource != null) {
            throw new UnsupportedOperationException("SqlManager cannot be re-initialized!");
        }

        jdbcUrl = prepareJdbcUrl(jdbcUrl);

        Optional<SqlService> optSqlService = Sponge.getServiceManager().provide(SqlService.class);
        if (!optSqlService.isPresent()) {
            throw new IllegalStateException("No sql service could be found!");
        }

        try {
            dataSource = optSqlService.get().getDataSource(jdbcUrl);
            accountsConnection= suppressedGetConnection(dataSource);
            jobsConnection = suppressedGetConnection(dataSource);
            commandConnection = suppressedGetConnection(dataSource);

            // Did the URL include a table prefix?
            // Or is one configured?
            Matcher dbPrefixMatcher = JDBC_DB_PREFIX.matcher(jdbcUrl);
            if (dbPrefixMatcher.find()) {
                tablePrefix = dbPrefixMatcher.group(1).substring(7);
            } else if (totalEconomy.getDatabasePrefix() != null) {
                tablePrefix = totalEconomy.getDatabasePrefix();
            }

        } catch (SQLException|TEConnectionException e) {
            logger.warn("Failed to initialize SQL data-source)!", e);
            close();
        }
    }

    /**
     * Ensures that a jdbc url includes certain parts by filling missing information from the TE instance.
     */
    private String prepareJdbcUrl(String jdbcUrl) {
        if (!jdbcUrl.startsWith("jdbc:")) {
            jdbcUrl = "jdbc:" + jdbcUrl;
        }

        Matcher dbNameMatcher = JDBC_DB_NAME.matcher(jdbcUrl);
        if (!dbNameMatcher.find() && dbNameMatcher.groupCount() <= 0) {
            jdbcUrl += jdbcUrl.endsWith("/") ? "totaleconomy" : "/totaleconomy";
        }

        Matcher dbUserMatcher = JDBC_DB_USERNAME.matcher(jdbcUrl);
        if (!dbUserMatcher.find()) {
            jdbcUrl += (jdbcUrl.contains("?") ? "&" : "?") + "user=" + totalEconomy.getDatabaseUser();
        }

        Matcher dbPassMatcher = JDBC_DB_PASSWORD.matcher(jdbcUrl);
        if (!dbPassMatcher.find()) {
            jdbcUrl += (jdbcUrl.contains("?") ? "&" : "?") + "password=" + totalEconomy.getDatabasePassword();
        }

        return jdbcUrl;
    }

    /**
     * Helper class to suppress the {@link SQLException} when getting a new connection from a {@link DataSource}
     */
    private Connection suppressedGetConnection(DataSource dataSource) {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new TEConnectionException("Failed to initialize sql connection.", e);
        }
    }

    /**
     * Returns the currently configured table prefix.
     * DO NOT CACHE
     */
    public Optional<String> getTablePrefix() {
        return Optional.ofNullable(tablePrefix);
    }

    /**
     * Returns the current data-source.
     * DO NOT CACHE!
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Returns the current connection for account data.
     * DO NOT CACHE!
     */
    public Connection getAccountsConnection() {
        return accountsConnection;
    }

    /**
     * Returns the current connection for job data.
     * DO NOT CACHE!
     */
    public Connection getJobsConnection() {
        return jobsConnection;
    }

    /**
     * Returns the current connection for {@link org.spongepowered.api.command.spec.CommandExecutor}s.
     * DO NOT CACHE!
     */
    public Connection getCommandConnection() {
        return commandConnection;
    }

    /**
     * Closes all connections and resets the fields to `null`.
     */
    @Override
    public void close() {
        dataSource = null;

        suppressedCloseConnectionIfNotNull(accountsConnection, "Failed to close accounts connection!");
        accountsConnection = null;

        suppressedCloseConnectionIfNotNull(jobsConnection, "Failed to close jobs connection!");
        jobsConnection = null;

        suppressedCloseConnectionIfNotNull(commandConnection, "Failed to close commands connection!");
        commandConnection = null;
    }

    private void suppressedCloseConnectionIfNotNull(Connection accountsConnection, String s) {
        try {
            if (accountsConnection != null && !accountsConnection.isClosed()) {
                accountsConnection.close();
            }
        } catch (SQLException e) {
            logger.warn(s, e);
        }
    }
}
