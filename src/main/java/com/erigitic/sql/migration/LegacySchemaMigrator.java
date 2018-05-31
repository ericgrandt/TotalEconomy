package com.erigitic.sql.migration;

import com.erigitic.main.TotalEconomy;
import com.erigitic.sql.SqlQuery;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Migrator from the "previous" (pre-sql-refactor) db schema to the "new" (version 1) db schema.
 *
 * In case the old database does contain no or less information than the current flat file storage
 *   it is more advisable to drop all tables to trigger flat file migration.
 *
 * @author MarkL4YG
 */
public class LegacySchemaMigrator implements SqlMigrator {

    private Logger logger;
    private TotalEconomy totalEconomy;

    // Process status information
    private final AtomicLong failures = new AtomicLong(0);
    private final AtomicLong importedBalances = new AtomicLong(0);
    private final AtomicLong importedJobProgress = new AtomicLong(0);
    private final List<MigrationException> exceptions = new CopyOnWriteArrayList<MigrationException>();

    // Actual process variables
    private final Connection[] connection = new Connection[1];
    private final Pattern UUID_PATTERN = Pattern.compile("([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-4[a-fA-F0-9]{3}-[89aAbB][a-fA-F0-9]{3}-[a-fA-F0-9]{12})");

    // Shared migration information
    private final Map<String, String> generatedUUIDs = new HashMap<>();
    private final List<String> jobs = new ArrayList<>();
    private final List<String> currencies = new ArrayList<>();

    @Override
    public void migrate(TotalEconomy totalEconomy) throws MigrationException {
        this.totalEconomy = totalEconomy;
        logger = totalEconomy.getLogger();
        logger.warn("Migration type: DBv0 -> DBv1");

        try {
            connection[0] = totalEconomy.getSqlManager().getDataSource().getConnection();
            connection[0].setAutoCommit(false);

            // 1. Move the old schema out of the way
            logger.info("Moving old schema...");
            moveOldSchema();

            // 2. Create the new schema
            logger.info("Creating new schema...");
            totalEconomy.getSqlManager().createTables();

            // This will update the `currencies` and `jobs` table so our FKs won't fail during the next steps
            totalEconomy.getSqlManager().postInitDatabase(totalEconomy.getJobManager());

            // 3. Import accounts from migration table
            // We'll drop the value for the job-notifications as it's really not that vital
            logger.info("Migrating accounts");
            importAccounts();

            // 4. Import virtual accounts from migration table
            logger.info("Migrating virtual accounts");
            importVirtualAccounts();

            // 5. Import currencies from migration table
            // In order to do that we need to get the column names first so we also import custom columns from the old format
            logger.info("Migrating currencies");
            importBalances();

            // 6. Import job progress from migration table
            // In order to do that we need to get the column names first so we also import custom columns from the old format
            logger.warn("Migrating job progress...");
            importJobsProgress();

            String query = "UPDATE `te_meta` SET `value` = 1 WHERE `ident` = 'schema_version'";
            try (SqlQuery updateQuery = new SqlQuery(connection[0], query)){
                PreparedStatement statement = updateQuery.getStatement();
                Integer updateCount = statement.executeUpdate();
                if (updateCount != 1 && updateCount != 0) {
                    throw new SQLException("Failed to update meta table! Update count: " + updateCount);
                }
            }

            logger.warn("Commiting transaction.");
            connection[0].commit();
            connection[0].close();
            logger.warn("DONE!");
            logger.info("Imported balance entries: " + importedBalances.get());
            logger.info("Imported progress entries: " + importedJobProgress.get());
            if (failures.get() > 0) {
                logger.warn("There have been failures: " + failures.get());
            }

            try {
                PrintStream stream = new PrintStream(new FileOutputStream(new File(totalEconomy.getConfigDir(), "migration.log")));
                exceptions.forEach(e -> e.printStackTrace(stream));
            } catch (IOException e) {
                logger.error("Failed to write migration log!", e);
            }

        } catch (Throwable e) {
            if (connection != null) {
                try {
                    connection[0].rollback();
                } catch (SQLException rbE) {
                    MigrationException ex = new MigrationException("Failed to rollback transaction!", rbE);
                    ex.addSuppressed(e);
                    throw ex;
                }
            }
            throw new MigrationException("Unknown error during migration - Transaction rolled back", e);
        }
    }

    /**
     * Moves all tables of the "old" schema out of the way.
     * This way their information can be used without conflicting with new tables.
     */
    private void moveOldSchema() throws SQLException {
        // Move the old schema out of the way so we can convert
        String query = "RENAME TABLE :first TO :last";
        String[][] tableNames = {
            {"accounts", "migr_accounts"},
            {"virtual_accounts", "migr_virtual_accounts"},
            {"levels", "migr_levels"},
            {"experience", "migr_experience"}
        };

        for (String[] table : tableNames) {
            try (Statement statement = connection[0].createStatement()) {
                String q = query.replaceAll(":first", table[0]);
                q = q.replaceAll(":last", table[1]);

                if (statement.executeUpdate(q) != 0) {
                    throw new SQLException("Unexpected update count");
                }
            }
        }
    }

    /**
     * Imports basic account information.
     */
    private void importAccounts() throws SQLException {
        String query = "SELECT * FROM migr_accounts";

        try (Statement statement = connection[0].createStatement()) {
            statement.executeQuery(query);
            String insertQuery = "INSERT INTO accounts (`uid`, `job`) VALUES (?, ?)";

            try (ResultSet result = statement.getResultSet()) {
                while (result.next()) {
                    String uid = result.getString("uid");
                    String job = result.getString("job").toLowerCase();
                    logger.debug(insertQuery, " / ", uid, ",", job);

                    try (PreparedStatement insertStatement = connection[0].prepareStatement(insertQuery)) {
                        insertStatement.setString(1, uid);
                        insertStatement.setString(2, job);
                        if (insertStatement.executeUpdate() != 1) {
                            throw new SQLException("Unexpected update count");
                        }
                    } catch (SQLException e) {
                        failures.incrementAndGet();
                        exceptions.add(new MigrationException("Failed to migrate account: " + uid, e));
                    }
                }
            }
        }
    }

    /**
     * Imports basic virtual account information
     */
    private void importVirtualAccounts() throws SQLException {
        String query = "SELECT * FROM migr_virtual_accounts";

        try (Statement statement = connection[0].createStatement()) {
            statement.executeQuery(query);
            String insertQuery = "INSERT INTO accounts (`uid`, `displayname`) VALUES (?, ?)";

            try (ResultSet result = statement.getResultSet()) {
                while (result.next()) {
                    String rawUID = result.getString("uid");
                    String sUUID;
                    String displayName;
                    Matcher matcher = UUID_PATTERN.matcher(rawUID);

                    // When a UUID has been found, use it
                    // When the key contained other information use that as the display name
                    if (matcher.find()) {
                        sUUID = matcher.group(1).toLowerCase();
                        displayName = UUID_PATTERN.matcher(rawUID).replaceAll("");
                    } else {
                        sUUID = UUID.randomUUID().toString();
                        displayName = rawUID;
                    }
                    generatedUUIDs.put(rawUID, sUUID);

                    try (PreparedStatement insertStatement = connection[0].prepareStatement(insertQuery)) {
                        insertStatement.setString(1, sUUID);
                        insertStatement.setString(2, displayName.isEmpty() ? "VIRTUAL_ACCOUNT" : displayName);
                        if (insertStatement.executeUpdate() != 1) {
                            throw new SQLException("Unexpected update count");
                        }
                    } catch (SQLException e) {
                        failures.incrementAndGet();
                        exceptions.add(new MigrationException("Failed to migrate virtual account: " + sUUID + "/" + displayName, e));
                    }
                }
            }
        }
    }

    /**
     * Handle the balance import.
     * This method searches the currencies as they previously were stored as columns.
     * With that information the actual import is triggered.
     *
     * Actual import is split between virtual and real accounts.
     * @see #importBalancesReal(String)
     * @see #importBalancesVirtual(String)
     */
    private void importBalances() throws SQLException {
        logger.info("Searching currencies...");
        String query = "SELECT `column_name` FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_SCHEMA` = ':db_name' AND `TABLE_NAME` = 'migr_accounts' AND `column_name` LIKE '%_balance'";

        try (Statement statement = connection[0].createStatement()) {
            query = query.replaceAll(":db_name", totalEconomy.getSqlManager().getDatabaseName());
            statement.executeQuery(query);

            try (ResultSet result = statement.getResultSet()) {
                while (result.next()) {
                    String column = result.getString(1);
                    currencies.add(column.replaceAll("_balance", ""));
                }
            }
        }
        StringBuilder columnString = new StringBuilder();
        columnString.append("uid");
        currencies.forEach(c -> columnString.append(",").append(c).append("_balance"));
        logger.info("The following columns were found:", columnString.toString());
        logger.warn("Migrating currencies");

        // Import currencies from player accounts
        importBalancesReal(columnString.toString());

        // Import currencies from virtual accounts
        importBalancesVirtual(columnString.toString());
    }

    /**
     * Import balances from real player accounts
     * @param columns The currency columns
     */
    private void importBalancesReal(String columns) throws SQLException {
        String query = "SELECT :columns FROM migr_accounts";
        query = query.replaceAll(":columns", columns);
        try (Statement statement = connection[0].createStatement()) {
            statement.executeQuery(query);

            try (ResultSet result = statement.getResultSet()) {
                while (result.next()) {
                    for (String bal : currencies) {
                        String uuid = result.getString("uid");
                        BigDecimal balance = result.getBigDecimal(bal + "_balance");
                        insertBalance(uuid, bal, balance);
                    }
                }
            }
        }
    }

    /**
     * Import balances from virtual accounts
     * @param columns The currency columns
     */
    private void importBalancesVirtual(String columns) throws SQLException {
        String query = "SELECT :columns FROM migr_virtual_accounts";
        query = query.replaceAll(":columns", columns);
        try (Statement statement = connection[0].createStatement()) {
            statement.executeQuery(query);

            try (ResultSet result = statement.getResultSet()) {
                while (result.next()) {
                    for (String bal : currencies) {
                        String sUUID = generatedUUIDs.get(result.getString("uid"));
                        BigDecimal balance = result.getBigDecimal(bal + "_balance");
                        insertBalance(sUUID, bal, balance);
                    }
                }
            }
        }
    }

    /**
     * Inserts a balance entry into the new schema.
     * @param uid The accounts {@link UUID#toString()}
     * @param currency The currencies name
     * @param balance The current balance of the account in this currency
     */
    private void insertBalance(String uid, String currency, BigDecimal balance) {
        String insertQuery = "INSERT INTO balances (`uid`, `currency`, `balance`) VALUES (?, ?, ?)";

        try (PreparedStatement insertStatement = connection[0].prepareStatement(insertQuery)) {
            insertStatement.setString(1, uid);
            insertStatement.setString(2, currency);
            insertStatement.setBigDecimal(3, balance);

            if (insertStatement.executeUpdate() != 1) {
                throw new SQLException("Unexpected update count");
            }
            importedBalances.incrementAndGet();
        } catch (SQLException e) {
            failures.incrementAndGet();
            exceptions.add(new MigrationException("Failed to migrate account balance " + currency + " on " + uid, e));
        }
    }

    /**
     * Imports job progress
     */
    public void importJobsProgress() throws SQLException {
        logger.info("Searching jobs...");
        String query = "SELECT `column_name` FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_SCHEMA` = ':db_name' AND `TABLE_NAME` = 'migr_levels'";

        try (Statement statement = connection[0].createStatement()) {
            query = query.replaceAll(":db_name", totalEconomy.getSqlManager().getDatabaseName());
            statement.executeQuery(query);

            try (ResultSet result = statement.getResultSet()) {
                while (result.next()) {
                    String column = result.getString(1);

                    if (!"uid".equals(column)) {
                        jobs.add(column);
                    }
                }
            }
        }
        StringBuilder colString = new StringBuilder();
        colString.append("uid");
        jobs.forEach(j -> colString.append(",").append(j));
        logger.info("The following columns were found:", colString.toString());
        logger.warn("Migrating job progression");
        query = "SELECT migr_levels.*, migr_experience.* FROM migr_levels LEFT JOIN migr_experience ON migr_levels.uid = migr_experience.uid";

        try (PreparedStatement statement = connection[0].prepareStatement(query)) {
            statement.executeQuery();

            try (ResultSet result = statement.getResultSet()) {
                while (result.next()) {
                    for (int index = 0; index < jobs.size(); index++) {
                        Integer leftIndex = 2;
                        Integer rightIndex = leftIndex + jobs.size() + 1;
                        String uid = result.getString(1);
                        String jobName = jobs.get(index);
                        Integer jobLevel = result.getInt(leftIndex);
                        Integer jobExp = result.getInt(rightIndex);
                        UUID jobUUID = totalEconomy.getJobManager().getJobUUIDByName(jobName).orElse(null);
                        String insertQuery = "INSERT INTO jobs_progress (`uid`, `job`, `level`, `experience`) VALUES (?, ?, ?, ?)";

                        if (jobUUID == null) {
                            exceptions.add(new MigrationException("Failed to migrate job progress for: " + jobName + ". No UUID found!"));
                            continue;
                        }

                        try (PreparedStatement insertStatement = connection[0].prepareStatement(insertQuery)) {
                            insertStatement.setString(1, uid);
                            insertStatement.setString(2, jobUUID.toString());
                            insertStatement.setInt(3, jobLevel);
                            insertStatement.setInt(4, jobExp);

                            if (insertStatement.executeUpdate() != 1) {
                                throw new SQLException("Unexpected update count");
                            }
                            importedJobProgress.incrementAndGet();
                        } catch (SQLException e) {
                            failures.incrementAndGet();
                            exceptions.add(new MigrationException("Failed to migrate job progress for " + uid + " on job " + jobName, e));
                        }
                    }
                }
            }
        }
    }
}
