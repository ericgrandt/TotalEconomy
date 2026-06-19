package com.ericgrandt.totaleconomy.exception;

/**
 * Base runtime exception for all account related errors.
 * <p>
 * This class serves as the superclass for specific exceptions such as {@link AccountNotFoundException}, etc. While
 * callers can catch this exception directly, it's recommended to catch the specific subclasses for more focused error
 * handling and logging.
 * </p>
 */
public abstract class AccountException extends RuntimeException {
    public AccountException() {
        super();
    }

    public AccountException(Throwable cause) {
        super(cause);
    }
}
