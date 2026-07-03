package com.ericgrandt.totaleconomy.exception;

public class AccountWithdrawException extends RuntimeException {
    public AccountWithdrawException() {
        super();
    }

    public AccountWithdrawException(Throwable cause) {
        super(cause);
    }
}
