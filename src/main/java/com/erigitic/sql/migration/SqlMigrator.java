package com.erigitic.sql.migration;

import com.erigitic.main.TotalEconomy;

/**
 * Interface for the migration process.
 * This may be expanded later if necessary.
 */
public interface SqlMigrator {

    void migrate(TotalEconomy totalEconomy) throws MigrationException;
}
