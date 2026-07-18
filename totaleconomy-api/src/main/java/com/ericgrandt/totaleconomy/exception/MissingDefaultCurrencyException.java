package com.ericgrandt.totaleconomy.exception;

/**
 * Thrown when the default currency cannot be located in storage.
 * <p>
 * This indicates a critical misconfiguration. The economy system requires at least one currency marked as default to
 * function. This should not occur under normal operation and typically indicates that storage was not properly seeded
 * during setup.
 * </p>
 * <p>
 * Resolution: Ensure a default currency is seeded automatically on startup or added manually to your storage medium
 * (e.g., database or config file).
 * </p>
 */
public class MissingDefaultCurrencyException extends TotalEconomyException {
    public MissingDefaultCurrencyException() {
        super();
    }
}
