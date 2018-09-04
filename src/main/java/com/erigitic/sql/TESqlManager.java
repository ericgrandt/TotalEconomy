package com.erigitic.sql;

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

    private Logger logger;
    private TotalEconomy totalEconomy;
    private DataSource dataSource;
    private Connection accountsConnection;
    private Connection jobsConnection;
    private String tablePrefix;

    public TESqlManager(TotalEconomy totalEconomy, Logger logger) {
        this.totalEconomy = totalEconomy;
        this.logger = logger;
    }

    /**
     * Initializes the connection with a database URL from the provided {@link TotalEconomy} instance.
     */
    public void initialize() {
        String jdbcUrl = totalEconomy.getDatabaseUrl();

        Matcher dbNameMatcher = JDBC_DB_NAME.matcher(jdbcUrl);
        if (!dbNameMatcher.find() && dbNameMatcher.groupCount() <= 0) {
            jdbcUrl += jdbcUrl.endsWith("/") ? "totaleconomy" : "/totaleconomy";
        }

        // TODO: Implement table prefix configuration
        tablePrefix = "tetest_";
        initialize(jdbcUrl);
    }

    /**
     * Initializes the SQLManager with a JDBC connection url.
     * Make sure the URL is valid beforehand.
     */
    public void initialize(String jdbcUrl) {
        if (jdbcUrl == null) {
            throw new IllegalArgumentException("Database URL may not be null!");
        }

        if (!jdbcUrl.startsWith("jdbc:")) {
            jdbcUrl = "jdbc:" + jdbcUrl;
        }

        Optional<SqlService> optSqlService = Sponge.getServiceManager().provide(SqlService.class);
        if (!optSqlService.isPresent()) {
            throw new IllegalStateException("No sql service could be found!");
        }

        DataSource dataSourceRep;
        Connection accountsConnectionRep;
        Connection jobsConnectionRep;
        try {
            if (dataSource != null) {
                close();
            }

            dataSourceRep = optSqlService.get().getDataSource(jdbcUrl);
            accountsConnectionRep = suppressedGetConnection(dataSourceRep);
            jobsConnectionRep = suppressedGetConnection(dataSourceRep);

            // No exceptions? Great.
            this.dataSource = dataSourceRep;
            this.accountsConnection = accountsConnectionRep;
            this.jobsConnection = jobsConnectionRep;
        } catch (SQLException e) {
            logger.warn("Failed to initialize SQL data-source)!", e);
        }
    }

    /**
     * Helper class to suppress the {@link SQLException} when getting a new connection from a {@link DataSource}
     */
    private Connection suppressedGetConnection(DataSource dataSource) {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.warn("Failed to initialize SQL connection!", e);
        }
        return null;
    }

    /**
     * Returns the currently configured table prefix.
     */
    public String getTablePrefix() {
        return tablePrefix;
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
     * Closes all connections and resets the fields to `null`.
     */
    @Override
    public void close() {
        dataSource = null;

        try {
            if (accountsConnection != null && !accountsConnection.isClosed()) {
                accountsConnection.close();
            }
        } catch (SQLException e) {
            logger.warn("Failed to close accounts connection!", e);
        }

        accountsConnection = null;

        try {
            if (jobsConnection != null && !jobsConnection.isClosed()) {
                jobsConnection.close();
            }
        } catch (SQLException e) {
            logger.warn("Failed to close jobs connection!", e);
        }

        jobsConnection = null;
    }
}
