package com.erigitic.sql.migration;

import com.erigitic.main.TotalEconomy;
import org.slf4j.Logger;

/**
 * Determines which Migrator to use
 *
 * @author MarkL4YG
 */
public class MigrationManager {

    private TotalEconomy totalEconomy;
    private Logger logger;
    private SqlMigrator migrator;

    public MigrationManager(TotalEconomy totalEconomy, Logger logger, Integer migrateFrom) {
        this.totalEconomy = totalEconomy;
        this.logger = logger;

        logger.info("Setting up for migration from schema version: " + migrateFrom);

        // Add new migrators here
        // This is to ensure a successful migration even when administrators skip versions.
        // For greater version jumps the migration may need to be re-run several times.
        switch (migrateFrom) {
            case -1: migrator = new FlatFileMigrator(); break;
            case 0: migrator = new LegacySchemaMigrator(); break;
            default: throw new RuntimeException("No migrator for: " + migrateFrom);
        }
    }

    public void run() {
        try {
            logger.warn("MIGRATING DATABASE - This may take a moment......");
            migrator.migrate(totalEconomy);
        } catch (MigrationException e) {
            throw new RuntimeException("Database migration failed", e);
        }
    }
}
