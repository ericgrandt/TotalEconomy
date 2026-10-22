package com.ericgrandt.totaleconomy.api.exception;

/**
 * Thrown when a requested currency cannot be located in storage.
 */
public class CurrencyNotFoundException extends TotalEconomyException {
    public CurrencyNotFoundException() {
        super();
    }
}
