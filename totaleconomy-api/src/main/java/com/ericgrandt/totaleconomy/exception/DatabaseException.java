package com.ericgrandt.totaleconomy.exception;

/**
 * Base runtime exception for all database related errors.
 */
public class DatabaseException extends TotalEconomyException {
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
