package com.erigitic.migrate.exec;

import com.erigitic.sql.TESqlManager;

import java.sql.Connection;

/**
 * Utility class and base template for migrations.
 * Implements runnable so we keep asynchronous migration in mind.
 */
public interface Migrator extends Runnable {

    /**
     * What schema version is required for this migration to run.
     */
    int fromVersion();

    /**
     * What schema version will be the result of this migration.
     */
    int toVersion();

    void setSqlManager(TESqlManager sqlManager);

    void setConnection(Connection connection);
}
