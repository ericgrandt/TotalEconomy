package com.ericgrandt.totaleconomy.exception;

/**
 * Thrown when a requested player account cannot be located in storage.
 * <p>
 * Callers should handle this exception by either creating the account automatically or notifying the user that they
 * don't have an account.
 * </p>
 */
public class AccountNotFoundException extends AccountException {
    public AccountNotFoundException() {
        super();
    }

    public AccountNotFoundException(Throwable cause) {
        super(cause);
    }
}
