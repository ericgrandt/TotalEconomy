package com.erigitic.sql.migration;

import com.erigitic.main.TotalEconomy;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MarkL4YG on 10-Jan-18
 */
public class LegacySchemaMigrator implements SQLMigrator {

    private Logger logger;

    @Override
    public void migrate(TotalEconomy totalEconomy) throws MigrationException {
        logger = totalEconomy.getLogger();
        logger.warn("Migration type: DBv0 -> DBv1");

        final AtomicLong failures = new AtomicLong(0);
        final AtomicLong importedBalances = new AtomicLong(0);
        final AtomicLong importedJobProgress = new AtomicLong(0);
        final List<MigrationException> exceptions = new CopyOnWriteArrayList<MigrationException>();

        Connection connection = null;

        try {
            connection = totalEconomy.getSqlManager().getDataSource().getConnection();
            connection.setAutoCommit(false);
            final Connection finalConnection = connection;

            // Move the old schema out of the way so we can convert
            logger.warn("Renaming old tables...");
            String query = "RENAME TABLE ? TO ?";
            String[][] tableNames = {
                {"accounts", "migr_accounts"},
                {"virtual_accounts", "migr_virtual_accounts"},
                {"levels", "migr_levels"},
                {"experience", "migr_experience"}
            };

            for (String[] table : tableNames) {
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, table[0]);
                    statement.setString(2, table[1]);

                    if (statement.executeUpdate() != 0) {
                        throw new SQLException("Unexpected update count");
                    }
                }
            }
            logger.warn("Creating new schema...");
            totalEconomy.getSqlManager().createTables();

            // This will update the `currencies` and `jobs` table so our FKs won't fail afterwards
            totalEconomy.getSqlManager().postInitDatabase(totalEconomy.getJobManager());

            // Import accounts from migration table
            // We'll drop the value for the job-notifications as it's really not that vital...
            logger.warn("Migrating accounts");
            query = "SELECT * FROM migr_accounts";

            try (Statement statement = connection.createStatement()) {
                statement.executeQuery(query);
                String insertQuery = "INSERT INTO accounts (`uid`, `job`) VALUES (?, ?)";

                do {
                    try (ResultSet result = statement.getResultSet()) {
                        if (result == null) {
                            break;
                        }
                        String uid = result.getString("uid");
                        String job = result.getString("job").toLowerCase();
                        logger.debug(insertQuery, " / ", uid, ",", job);

                        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
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
                } while (statement.getMoreResults());
            }

            // Import virtual accounts from migration table
            logger.warn("Migrating virtual accounts");
            query = "SELECT * FROM virtual_accounts";

            try (Statement statement = connection.createStatement()) {
                statement.executeQuery(query);
                String insertQuery = "INSERT INTO accounts (`uid`, `displayname`) VALUES (?, ?)";
                final Pattern UUID_PATTERN = Pattern.compile("([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-4[a-fA-F0-9]{3}-[89aAbB][a-fA-F0-9]{3}-[a-fA-F0-9]{12})");

                do {
                    try (ResultSet result = statement.getResultSet()) {
                        if (result == null) {
                            break;
                        }
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
                        logger.debug(insertQuery, " / ", sUUID, ",", displayName);

                        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
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
                } while (statement.getMoreResults());
            }

            // Import job progress from migration table
            // In order to do that we need to get the column names first so we also import custom columns from the old format
            logger.warn("Migrating job progress");
            logger.info("Searching jobs...");
            List<String> jobs = new ArrayList<>();

            try (Statement statement = connection.createStatement()) {
                query = "SELECT `column_name` FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_SCHEMA` = :db_name AND `TABLE_NAME` = 'levels'";
                query = query.replaceAll(":db_name", totalEconomy.getSqlManager().getDatabaseName());
                statement.executeQuery(query);

                do {
                    try (ResultSet result = statement.getResultSet()) {
                        if (result == null) {
                            break;
                        }
                        String column = result.getString(1);

                        if (!"uid".equals(column)) {
                            jobs.add(column);
                        }
                    }
                } while (statement.getMoreResults());
            }
            StringBuilder colString = new StringBuilder();
            colString.append("uid");
            jobs.forEach(j -> colString.append(",").append(j));
            logger.info("The following columns were found:", colString.toString());
            logger.warn("Migrating job progression");
            query = "SELECT levels.*, experience.* FROM levels LEFT JOIN experience ON levels.uid = experience.uid";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.executeQuery();

                do {
                    try (ResultSet result = statement.getResultSet()) {
                        if (result == null) {
                            return;
                        }
                        for (int index = 0; index < jobs.size(); index++) {
                            Integer leftIndex = index + 1;
                            Integer rightIndex = leftIndex + jobs.size() + 1;
                            String uid = result.getString(1);
                            String jobName = jobs.get(index);
                            Integer jobLevel = result.getInt(leftIndex);
                            Integer jobExp = result.getInt(rightIndex);
                            String insertQuery = "INSERT INTO jobs_progress (`uid`, `job`, `level`, `experience`) VALUES (?, ?, ?, ?)";

                            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                                insertStatement.setString(1, uid);
                                insertStatement.setString(2, jobName);
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
                } while (statement.getMoreResults());
            }

            logger.warn("Commiting transaction.");
            connection.commit();
            logger.warn("DONE!");

            if (failures.get() > 0) {
                logger.warn("There have been failures: " + failures.get());
            }

            try {
                PrintStream stream = new PrintStream(new FileOutputStream(new File(totalEconomy.getConfigDir(), "migration.log")));
                exceptions.forEach(e -> e.printStackTrace(stream));
            } catch (IOException e) {
                logger.error("Failed to write migration log!", e);
            }
            logger.info("Imported balance entries: " + importedBalances.get());
            logger.info("Imported progress entries: " + importedJobProgress.get());

        } catch (Throwable e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rbE) {
                    MigrationException ex = new MigrationException("Failed to rollback transaction!", rbE);
                    ex.addSuppressed(e);
                    throw ex;
                }
            }
            throw new MigrationException("Unknown error during migration - Transaction rolled back", e);
        }
    }
}
