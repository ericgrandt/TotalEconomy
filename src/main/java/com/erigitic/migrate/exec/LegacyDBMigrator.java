package com.erigitic.migrate.exec;

import com.erigitic.except.TEMigrationException;
import com.erigitic.migrate.util.ColumnBuilder;
import com.erigitic.migrate.util.ConstraintBuilder;
import com.erigitic.migrate.util.TableBuilder;
import org.intellij.lang.annotations.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.service.economy.Currency;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Migrates from the legacy database format.
 * (Pre sql-refactor)
 */
public class LegacyDBMigrator extends AbstractDBMigrator {

    private static final Logger logger = LoggerFactory.getLogger(LegacyDBMigrator.class);
    private static final Pattern UUID_PATTERN = Pattern.compile("([a-f0-9]{8}(?:-[a-f0-9]{4}){3}-[a-f0-9]{11})");

    private String tempVirtualAccountsTable;
    private String tempAccountsTable;
    private String tempExperienceTable;
    private String tempLevelsTable;
    private String metaTableName;
    private String accountTableName;
    private String currenciesTableName;
    private String jobsTableName;
    private String balancesTableName;
    private String jobStatTableName;

    private final List<String> legacyAccounts = new LinkedList<>();
    private final Map<String, UUID> legacyVAccounts = new HashMap<>();

    @Override
    public int fromVersion() {
        return 0;
    }

    @Override
    public int toVersion() {
        return 1;
    }

    @Override
    public void run() {
        moveLegacyTables();
        createTables();
        migrateData();
        setMetaInformation();
    }

    private void moveLegacyTables() {
        logger.info("Moving legacy tables out the way...");
        tempAccountsTable = getEffectiveTableName("accounts_legacy");
        tempVirtualAccountsTable = getEffectiveTableName("vaccounts_legacy");
        tempExperienceTable = getEffectiveTableName("experience_legacy");
        tempLevelsTable = getEffectiveTableName("levels_legacy");

        renameTable("accounts", tempAccountsTable);
        renameTable("virtual_accounts", tempVirtualAccountsTable);
        renameTable("experience", tempExperienceTable);
        renameTable("levels", tempLevelsTable);
    }

    private void createTables() {
        logger.info("Creating new table schema...");
        metaTableName = getEffectiveTableName("meta");
        accountTableName = getEffectiveTableName("accounts");
        currenciesTableName = getEffectiveTableName("currencies");
        jobsTableName = getEffectiveTableName("jobs");
        balancesTableName = getEffectiveTableName("balances");
        jobStatTableName = getEffectiveTableName("job_stats");

        createTable(accountTableName,
                new TableBuilder()
                        .column(ColumnBuilder.string("uuid").length(60).notNull())
                        .column(ColumnBuilder.string("alias").length(120).defaultValue("NULL"))
                        .constraint(ConstraintBuilder.primary().column("uuid"))
                        .constraint(ConstraintBuilder.unique().column("alias")));

        createTable(jobsTableName,
                new TableBuilder()
                        .column(ColumnBuilder.string("uuid").length(60).notNull())
                        .constraint(ConstraintBuilder.primary().column("uuid"))
        );

        createTable(currenciesTableName,
                new TableBuilder()
                        .column(ColumnBuilder.string("uuid").length(60).notNull())
                        .constraint(ConstraintBuilder.primary().column("uuid"))
        );

        createTable(balancesTableName,
                new TableBuilder()
                        .column(ColumnBuilder.string("account").length(60).notNull())
                        .column(ColumnBuilder.string("currency").length(60).notNull())
                        .column(ColumnBuilder.integer("balance").notNull().defaultValue("0"))
                        .constraint(ConstraintBuilder.primary().column("account"))
                        .constraint(ConstraintBuilder.foreignKey().column("account").references("uuid").on(accountTableName))
                        .constraint(ConstraintBuilder.foreignKey().column("currency").references("uuid").on(currenciesTableName))
                        .constraint(ConstraintBuilder.unique().columns("account", "currency"))
        );

        createTable(jobStatTableName,
                new TableBuilder()
                        .column(ColumnBuilder.string("account").length(60).notNull())
                        .column(ColumnBuilder.string("job").length(60).notNull())
                        .column(ColumnBuilder.integer("experience").defaultValue("0"))
                        .column(ColumnBuilder.integer("levels").defaultValue("0"))
                        .constraint(ConstraintBuilder.primary().column("account"))
                        .constraint(ConstraintBuilder.unique().columns("account", "job"))
                        .constraint(ConstraintBuilder.foreignKey().column("account").references("uuid").on(accountTableName))
                        .constraint(ConstraintBuilder.foreignKey().column("job").references("uuid").on(jobsTableName))
        );

        createTable(metaTableName,
                new TableBuilder()
                        .column(ColumnBuilder.string("ident").length(160).notNull())
                        .column(ColumnBuilder.string("value").length(510))
                        .constraint(ConstraintBuilder.primary().column("ident"))
        );
    }

    private void migrateData() {
        foreignKeyChecks(false);
        migrateDataAccounts();
        migrateDataCurrencies();
        migrateDataBalances();
        migrateDataJobs();
        migrateDataJobExp();
        foreignKeyChecks(true);
    }

    private void migrateDataAccounts() {
        logger.info("Migrating accounts...");

        try (PreparedStatement statement = prepare("SELECT uuid FROM ?")) {
            statement.setString(1, tempAccountsTable);
            collectStrings(statement.executeQuery(), legacyAccounts);
        } catch (SQLException e) {
            throw new TEMigrationException("Failed to read legacy account UUIDs!", e);
        }

        try (PreparedStatement statement = prepare("SELECT uid FROM ?")) {
            statement.setString(1, tempVirtualAccountsTable);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String account = resultSet.getString(1);
                Matcher matcher = UUID_PATTERN.matcher(account);
                UUID uuid;
                if (!matcher.find()) {
                    uuid = UUID.randomUUID();
                    logger.warn("Virtual account has no UUID. Generated: \"{}\" for account \"{}\"", uuid, account);
                } else {
                    uuid = UUID.fromString(matcher.group(1));
                }
                legacyVAccounts.put(account, uuid);
            }

        } catch (SQLException e) {
            throw new TEMigrationException("Failed to read legacy virtual account UUIDs!", e);
        }

        @Language("MySQL")
        String query = "INSERT INTO ? (uuid, display_name) VALUES (?, ?)";
        try (PreparedStatement statement = prepare(query)) {
            statement.setString(1, accountTableName);
            for (String account : legacyAccounts) {
                statement.setString(2, account);
                statement.setString(3, null);
                statement.executeUpdate();
            }

            for (Map.Entry<String, UUID> entry : legacyVAccounts.entrySet()) {
                String account = entry.getKey();
                UUID uuid = entry.getValue();
                statement.setString(1, uuid.toString());
                statement.setString(2, account);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new TEMigrationException("Failed to migrate account data!");
        }
    }

    private void migrateDataCurrencies() {
        logger.debug("Inserting currencies for FK constraints...");
        @Language("MySQL")
        String query = "INSERT INTO " + currenciesTableName + " (ident) VALUES (?)";
        try (PreparedStatement statement = prepare(query)) {
            getPlugin().getCurrencies().forEach(currency -> {
                try {
                    statement.setString(1, currency.getId());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    throw new TEMigrationException("Failed to insert currency: " + currency.getId(), e);
                }
            });
        } catch (SQLException e) {
            throw new TEMigrationException("Failed to prepare insert-query", e);
        }
    }

    private void migrateDataBalances() {
        getPlugin().getCurrencies().forEach(this::migrateDataCurrencyBalance);
    }

    private void migrateDataCurrencyBalance(Currency currency) {
        logger.info("Migrating balances for: {}", currency.getId());
        String legacyColumnName = currency.getName() + "_balance";
        @Language("MySQL")
        String query = "INSERT INTO " + balancesTableName + "(currency_ident, account_uuid, balance) VALUES (?, ?, ?)";
        try (PreparedStatement statement = prepare(query)) {
            statement.setString(1, currency.getId());

            for (String account : legacyAccounts) {
                logger.debug("Migrating account: {}", account);
                statement.setString(2, account);
                statement.setInt(3, getLegacyBalance(account, legacyColumnName, false));
            }

            for (Map.Entry<String, UUID> entry : legacyVAccounts.entrySet()) {
                String account = entry.getKey();
                UUID uuid = entry.getValue();
                logger.debug("Migrating virtual account: {}", account);
                statement.setString(2, uuid.toString());
                statement.setInt(3, getLegacyBalance(account, legacyColumnName, true));
            }

        } catch (SQLException e) {
            throw new TEMigrationException("Failed to migrate balances!", e);
        }
    }

    private void migrateDataJobs() {
        logger.info("Inserting jobs for FK constraints...");
        @Language("MySQL")
        String query = "INSERT INTO ? (ident) VALUES (?)";
        try (PreparedStatement statement = prepare(query)) {
            Set<String> jobs = getPlugin().getJobManager().getJobs().keySet();
            for (String job : jobs) {
                statement.setString(1, jobsTableName);
                statement.setString(2, job);
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new TEMigrationException("Failed to migrate jobs!", e);
        }
    }

    private void migrateDataJobExp() {
        logger.info("Migrating job experience...");
        Set<String> jobs = getPlugin().getJobManager().getJobs().keySet();

        for (String job : jobs) {
            if (tableHasColumn(tempExperienceTable, job) && tableHasColumn(tempLevelsTable, job)) {
                for (String account : legacyAccounts) {
                    migrateDataJobExpForAccountAndJob(account, job);
                }
            } else {
                logger.warn("No legacy experience or levels column for job \"{}\"", job);
            }
        }
    }

    private void migrateDataJobExpForAccountAndJob(String account, String job) {
        logger.debug("Migrating job \"{}\" for uuid \"{}\"", account, job);
        int experience = 0;
        int level = 0;

        @Language("MySQL")
        String query = "SELECT ? FROM ? WHERE `uid` = ?";
        try (PreparedStatement statement = prepare(query)) {
            statement.setString(1, job);
            statement.setString(2, tempExperienceTable);
            statement.setString(3, account);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                experience = resultSet.getInt(1);
            } else {
                logger.debug("No experience data found for account \"{}\" and job \"{}}\" using 0.", account, job);
            }

            statement.setString(2, tempLevelsTable);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                level = resultSet.getInt(1);
            } else {
                logger.debug("No level data found for account \"{}\" and job \"{}}\" using 0.", account, job);
            }
        } catch (SQLException e) {
            logger.warn("Failed to migrate job data for user \"{}\" and job \"{}\"", account, job);
        }

        query = "INSERT INTO ? (account_uuid, job_ident, experience, level) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = prepare(query)) {
            statement.setString(1, jobStatTableName);
            statement.setString(2, account);
            statement.setString(3, job);
            statement.setInt(4, experience);
            statement.setInt(5, level);
            statement.executeUpdate();

        } catch (SQLException e) {
            logger.warn("Failed to insert job data for user \"{}\" and job \"{}\"", account, job);
        }
    }

    private void collectStrings(ResultSet set, List<String> collection) throws SQLException {
        while (set.next()) {
            collection.add(set.getString(1));
        }
    }

    private int getLegacyBalance(String uuid, String columnName, boolean isVirtualAccount) {
        String tableName = isVirtualAccount ? tempVirtualAccountsTable : tempAccountsTable;

        if (tableHasColumn(tableName, columnName)) {

            @Language("MySQL")
            String query = "SELECT " + columnName + " FROM " + tableName + " WHERE `uid` = ?";
            try (PreparedStatement statement = prepare(query)) {
                statement.setString(1, uuid);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    throw new SQLException("No result for query.");
                }

            } catch (SQLException e) {
                logger.warn("Failed to retrieve \"{}\" for \"{}\"!", columnName, uuid);
            }
        } else {
            logger.warn("Currency column not available: {}", columnName);
        }
        return 0;
    }

    private void setMetaInformation() {
        @Language("MySQL")
        String query = "INSERT INTO ? (ident, value) VALUES ('schema_version', ?) ON DUPLICATE KEY UPDATE value = VALUES(value)";
        try (PreparedStatement statement = prepare(query)) {
            statement.setString(1, metaTableName);
            statement.setInt(2, toVersion());
        } catch (SQLException e) {
            throw new TEMigrationException("Failed to update meta table!", e);
        }
    }
}
