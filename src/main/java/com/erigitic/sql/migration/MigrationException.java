package com.erigitic.sql.migration;

/**
 * Exclusive exception for the migration process.
 * This unifies the throws-clauses in methods.
 * @see #getCause() for the underlying cause.
 */
public class MigrationException extends Exception {

    public MigrationException(String message) {
        super(message);
    }

    public MigrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
