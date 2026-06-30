package com.ericgrandt.totaleconomy.exception;

/**
 * Base runtime exception for all database related errors.
 */
public class DatabaseException extends RuntimeException {
    public DatabaseException() {
        super();
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseException(Throwable cause) {
        super(cause);
    }
}
