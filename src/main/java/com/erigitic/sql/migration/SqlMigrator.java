package com.erigitic.sql.migration;

import com.erigitic.main.TotalEconomy;

/**
 * Created by MarkL4YG on 10-Jan-18
 */
public interface SqlMigrator {

    void migrate(TotalEconomy totalEconomy) throws MigrationException;
}
