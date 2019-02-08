package com.erigitic.migrate.exec;

import com.erigitic.main.TotalEconomy;

/**
 * Utility class and base template for migrations.
 * Implements runnable so we keep asynchronous migration in mind for now.
 */
public abstract class Migrator implements Runnable {

    private TotalEconomy plugin;

    /**
     * What schema version is required for this migration to run.
     */
    abstract int fromVersion();

    /**
     * What schema version will be the result of this migration.
     */
    abstract int toVersion();

    public void setPlugin(TotalEconomy plugin) {
        this.plugin = plugin;
    }

    protected TotalEconomy getPlugin() {
        return plugin;
    }
}
