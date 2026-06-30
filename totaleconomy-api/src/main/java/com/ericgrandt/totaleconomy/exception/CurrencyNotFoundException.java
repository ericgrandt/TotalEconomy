package com.ericgrandt.totaleconomy.exception;

/**
 * Thrown when a requested currency cannot be located in storage.
 */
public class CurrencyNotFoundException extends CurrencyException {
    public CurrencyNotFoundException() {
        super();
    }

    public CurrencyNotFoundException(Throwable cause) {
        super(cause);
    }
}
