package com.erigitic.sql.migration;

import com.erigitic.main.TotalEconomy;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

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
public class FlatFileMigrator implements SQLMigrator {

    private Logger logger;

    @Override
    public void migrate(TotalEconomy totalEconomy) throws MigrationException {

        logger = totalEconomy.getLogger();
        logger.warn("Migration type: Flat file -> DB");

        final AtomicLong failures = new AtomicLong(0);
        final AtomicLong importedBalances = new AtomicLong(0);
        final AtomicLong importedJobProgress = new AtomicLong(0);
        final List<MigrationException> exceptions = new CopyOnWriteArrayList<MigrationException>();

        Connection connection = null;

        try {
            File configDir = totalEconomy.getConfigDir();

            File accountsConfigFile = new File(configDir, "accounts.conf");
            HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                                                                      .setFile(accountsConfigFile)
                                                                      .build();
            ConfigurationNode accountsConfig = loader.load();
            connection = totalEconomy.getSqlManager().getDataSource().getConnection();
            connection.setAutoCommit(false);
            final Connection finalConnection = connection;

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

                              // Virtual accounts won't necessarily have a valid UUID. We'll try to convert those at some other place though.
                              UUID uid = null;
                              try {
                                  uid = UUID.fromString(((String) oKey));
                              } catch (IllegalArgumentException e) {
                                  exceptions.add(new MigrationException("Cannot init account uid (legacy virtual?): " + oKey, e));
                                  return;
                              }
                              String query = "INSERT INTO accounts (`uid`, `displayname`, `job`) VALUES (?, ?, ?)";

                              // Insert the new account into the table
                              try (PreparedStatement statement = finalConnection.prepareStatement(query)) {
                                  statement.setString(1, uid.toString());
                                  statement.setString(2, accountNode.getNode("displayname").getString("E_ACC_NAME"));
                                  statement.setString(3, accountNode.getNode("job").getString("NULL"));

                                  if (statement.executeUpdate() != 1) {
                                      failures.incrementAndGet();
                                      throw new SQLException("Unexpected update count");
                                  }
                              } catch (SQLException e) {
                                  exceptions.add(new MigrationException("Failed to create account " + uid, e));
                              }
                              boolean searchString = false;
                              Set<? extends Map.Entry<Object, ? extends ConfigurationNode>> entries;

                              // Check if we have the new or old balance safe format
                              if (accountNode.getNode("balance").isVirtual()) {
                                  entries = accountNode.getChildrenMap().entrySet();
                                  searchString = true;
                              } else {
                                  entries = accountNode.getNode("balance").getChildrenMap().entrySet();
                              }
                              query = "INSERT INTO balances (`uid`, `currency`, `balance`) VALUES (?, ?, ?)";

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
                                  try (PreparedStatement statement = finalConnection.prepareStatement(query)) {
                                      statement.setString(1, uid.toString());
                                      statement.setString(2, ((String) balKey).toLowerCase());
                                      statement.setString(3, balEntry.getValue().getString());

                                      if (statement.executeUpdate() != 1) {
                                          failures.incrementAndGet();
                                          throw new SQLException("Unexpected update count");
                                      }

                                      importedBalances.incrementAndGet();
                                  } catch (Exception e) {
                                      exceptions.add(new MigrationException("Failed to register balance for " + balKey + " on " + uid, e));
                                  }
                              }
            });
            connection.commit();

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
        logger.info("Imported balance entries: " + importedBalances.get());
        logger.info("Imported progress entries: " + importedJobProgress.get());

        if (failures.get() > 0) {
            Sponge.getServer().shutdown(Text.of(TextColors.RED, "[TotalEconomy] Migration partially finished. Admin: Please review your migration.log for the error list!"));
            logger.warn("Migration partially finished. Admin: Please review your migration.log for the error list!");
        } else {
            Sponge.getServer().shutdown(Text.of(TextColors.GREEN, "[TotalEconomy] Migration finished. Admin: Please start the server."));
            logger.info("Migration finished. Admin: Please start the server.");
        }
        try {
            PrintStream stream = new PrintStream(new FileOutputStream(new File(totalEconomy.getConfigDir(), "migration.log")));
            exceptions.forEach(e -> e.printStackTrace(stream));
        } catch (IOException e) {
            logger.error("Failed to write migration log", e);
        }
    }
}
