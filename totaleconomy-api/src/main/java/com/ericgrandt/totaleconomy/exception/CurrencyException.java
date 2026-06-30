package com.ericgrandt.totaleconomy.exception;

/**
 * Base runtime exception for all currency related errors.
 * <p>
 * This class serves as the superclass for specific exceptions such as {@link CurrencyNotFoundException},
 * {@link MissingDefaultCurrencyException}, etc. While callers can catch this exception directly, it's recommended to
 * catch the specific subclasses for more focused error handling and logging.
 * </p>
 */
public abstract class CurrencyException extends RuntimeException {
    public CurrencyException() {
        super();
    }

    public CurrencyException(Throwable cause) {
        super(cause);
    }
}
