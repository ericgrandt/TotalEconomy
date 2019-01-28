package com.erigitic.migrate.exec;

import com.erigitic.except.TEMigrationException;
import com.erigitic.migrate.util.ColumnBuilder;
import com.erigitic.migrate.util.ConstraintBuilder;
import com.erigitic.migrate.util.TableBuilder;
import com.sun.javaws.exceptions.InvalidArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Migrates from the legacy database format.
 * (Pre sql-refactor)
 */
public class LegacyDBMigrator extends AbstractDBMigrator {

    private static final Logger logger = LoggerFactory.getLogger(LegacyDBMigrator.class);

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

        foreignKeyChecks(true);
    }

    private void migrateDataAccounts() {
        List<String> accounts = new LinkedList<>();
        List<String> virtualAccounts = new LinkedList<>();

        try (PreparedStatement statement = prepare("SELECT uuid FROM ?")) {
            statement.setString(1, tempAccountsTable);
            collectStrings(statement.executeQuery(), accounts);
        } catch (SQLException e) {
            throw new TEMigrationException("Failed to read legacy account UUIDs!", e);
        }

        try (PreparedStatement statement = prepare("SELECT uid FROM ?")) {
            statement.setString(1, tempVirtualAccountsTable);
            collectStrings(statement.executeQuery(), virtualAccounts);
        } catch (SQLException e) {
            throw new TEMigrationException("Failed to read legacy virtual account UUIDs!", e);
        }

        throw new UnsupportedOperationException("");
    }

    private void collectStrings(ResultSet set, List<String> collection) throws SQLException {
        while (set.next()) {
            collection.add(set.getString(1));
        }
    }

    private void setMetaInformation() {

    }
}
