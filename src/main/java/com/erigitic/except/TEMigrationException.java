package com.erigitic.except;

public class TEMigrationException extends TERuntimeException {

    public TEMigrationException() {
    }

    public TEMigrationException(String message) {
        super(message);
    }

    public TEMigrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TEMigrationException(Throwable cause) {
        super(cause);
    }

    public TEMigrationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
