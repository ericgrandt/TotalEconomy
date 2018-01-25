package com.erigitic.sql.migration;

import com.erigitic.main.TotalEconomy;
import com.erigitic.sql.SqlQuery;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Migrator from flat file storage to DB.
 * Update whenever you update the schema.
 *
 * This migrator assumes the latest database schema already to be applied.
 * In that case it is advisable to run the database creation of the SQLManager on an empty database beforehand
 * (Default implementation actually does that.)
 *
 * @author MarkL4YG
 */
public class FlatFileMigrator implements SqlMigrator {

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
    private final Map<String, String> generatedUUIDs = new ConcurrentHashMap<>();

    @Override
    public void migrate(TotalEconomy totalEconomy) throws MigrationException {
        this.totalEconomy = totalEconomy;
        logger = totalEconomy.getLogger();
        logger.warn("Migration type: Flat file -> DB");

        try {
            // Load current configuration
            File configDir = totalEconomy.getConfigDir();
            File accountsConfigFile = new File(configDir, "accounts.conf");
            HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                                                                      .setFile(accountsConfigFile)
                                                                      .build();
            ConfigurationNode accountsConfig = loader.load();
            connection[0] = totalEconomy.getSqlManager().getDataSource().getConnection();
            connection[0].setAutoCommit(false);

            // This will update the `currencies` and `jobs` table so our FKs won't fail afterwards
            totalEconomy.getSqlManager().postInitDatabase(totalEconomy.getJobManager());

            accountsConfig.getChildrenMap()
                          .entrySet()
                          .parallelStream()
                          .forEach(entry -> {
                              Object oKey = entry.getKey();
                              ConfigurationNode accountNode = entry.getValue();

                              if (!(oKey instanceof String)) {
                                  exceptions.add(new MigrationException("Cannot migrate account - Key not string!"));
                                  return;
                              }

                              // 1. Import accounts from node
                              // We'll drop the value for the job-notifications as it's really not that vital
                              importAccount(accountNode);

                              // 2. Import balances
                              importBalances(accountNode);

                              // 3. Import job stats
                              importJobProgress(accountNode);
            });

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

        if (failures.get() > 0) {
            Sponge.getServer().shutdown(Text.of(TextColors.RED, "[TotalEconomy] Migration partially finished. Admin: Please review your migration.log for the error list!"));
            logger.warn("Migration partially finished. Admin: Please review your migration.log for the error list!");
        } else {
            Sponge.getServer().shutdown(Text.of(TextColors.GREEN, "[TotalEconomy] Migration finished. Admin: Please start the server."));
            logger.info("Migration finished. Admin: Please start the server.");
        }
    }

    private void importAccount(ConfigurationNode accountNode) {
        // Virtual accounts won't necessarily have a valid UUID. We'll try to convert those at some other place though.
        String rawUID = ((String) accountNode.getKey());
        String sUUID;
        String displayName = accountNode.getNode("displayname").getString(null);
        Matcher matcher = UUID_PATTERN.matcher(rawUID);

        // When a UUID has been found, use it
        // When the key contained other information use that as the display name
        if (matcher.find()) {
            sUUID = matcher.group(1).toLowerCase();

            if (sUUID.length() != rawUID.length()) {
                displayName = UUID_PATTERN.matcher(rawUID).replaceAll("");
            }
        } else {
            sUUID = UUID.randomUUID().toString();
            displayName = rawUID;
        }
        generatedUUIDs.put(rawUID, sUUID);
        String query = "INSERT INTO accounts (`uid`, `displayname`, `job`) VALUES (?, ?, ?)";

        // Insert the new account into the table
        try (PreparedStatement statement = connection[0].prepareStatement(query)) {
            statement.setString(1, sUUID);
            statement.setString(2, displayName);
            statement.setString(3, accountNode.getNode("job").getString(null));

            if (statement.executeUpdate() != 1) {
                throw new SQLException("Unexpected update count");
            }
        } catch (SQLException e) {
            failures.incrementAndGet();
            exceptions.add(new MigrationException("Failed to create account " + sUUID, e));
        }
    }

    private void importBalances(ConfigurationNode accountNode) {
        Set<? extends Map.Entry<Object, ? extends ConfigurationNode>> entries;
        String sUUID = generatedUUIDs.get((String) accountNode.getKey());
        boolean searchString = false;

        // Check if we have the new or old balance safe format
        if (accountNode.getNode("balance").isVirtual()) {
            entries = accountNode.getChildrenMap().entrySet();
            searchString = true;
        } else {
            entries = accountNode.getNode("balance").getChildrenMap().entrySet();
        }
        String query = "INSERT INTO balances (`uid`, `currency`, `balance`) VALUES (?, ?, ?)";

        // Insert balances into the table
        for (Map.Entry<Object, ? extends ConfigurationNode> balEntry : entries) {
            Object balKey = balEntry.getKey();

            if (!(balKey instanceof String)) {
                continue;
            }

            if (searchString) {
                if (!((String) balKey).endsWith("-balance")) {
                    continue;
                }
                balKey = ((String) balKey).replaceAll("-balance", "");
            }

            // Insert balance
            try (PreparedStatement statement = connection[0].prepareStatement(query)) {
                statement.setString(1, sUUID);
                statement.setString(2, ((String) balKey).toLowerCase());
                statement.setString(3, balEntry.getValue().getString());

                if (statement.executeUpdate() != 1) {
                    throw new SQLException("Unexpected update count");
                }

                importedBalances.incrementAndGet();
            } catch (SQLException e) {
                failures.incrementAndGet();
                exceptions.add(new MigrationException("Failed to register balance for " + balKey + " on " + sUUID, e));
            }
        }
    }

    private void importJobProgress(ConfigurationNode accountNode) {
        String sUUID = generatedUUIDs.get(((String) accountNode.getKey()));
        ConfigurationNode statsNode = accountNode.getNode("jobstats");

        if (!statsNode.isVirtual()) {
            Set<? extends Map.Entry<Object, ? extends ConfigurationNode>> entries = statsNode.getChildrenMap().entrySet();

            for (Map.Entry<Object, ? extends ConfigurationNode> entry : entries) {
                Object oKey = entry.getKey();
                ConfigurationNode value = entry.getValue();

                if (!(oKey instanceof String)) {
                    continue;
                }
                String query = "INSERT INTO jobs_progress (`uid`, `job`, `level`, `experience`) VALUES (?, ?, ?, ?)";
                UUID jobUUID = totalEconomy.getJobManager().getJobUUIDByName(((String) oKey)).orElse(null);

                if (jobUUID == null) {
                    exceptions.add(new MigrationException("Failed to migrate job progress for: " + oKey + ". No UUID found!"));
                    continue;
                }

                try (PreparedStatement statement = connection[0].prepareStatement(query)) {
                    statement.setString(1, sUUID);
                    statement.setString(2, jobUUID.toString());
                    statement.setInt(3, value.getNode("level").getInt(0));
                    statement.setInt(4, value.getNode("exp").getInt(0));

                    if (statement.executeUpdate() != 1) {
                        throw new SQLException("Unexpected update count");
                    }

                    importedJobProgress.incrementAndGet();
                } catch (SQLException e) {
                    failures.incrementAndGet();
                    exceptions.add(new MigrationException("Failed to insert job progress for " + sUUID + " on " + oKey, e));
                }
            }
        }
    }
}
