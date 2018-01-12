package com.erigitic.sql.migration;

/**
 * Created by MarkL4YG on 10-Jan-18
 */
public class MigrationException extends Exception {

    public MigrationException(String message) {
        super(message);
    }

    public MigrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
