package com.ericgrandt.totaleconomy.exception;

/**
 * Base runtime exception for all database related errors.
 */
public abstract class DatabaseException extends RuntimeException {
    public DatabaseException() {
        super();
    }

    public DatabaseException(Throwable cause) {
        super(cause);
    }
}
