package com.ericgrandt.totaleconomy.exception;

public class TotalEconomyException extends RuntimeException {
    public TotalEconomyException() {
        super();
    }

    public TotalEconomyException(String message) {
        super(message);
    }

    public TotalEconomyException(Throwable cause) {
        super(cause);
    }

    public TotalEconomyException(String message, Throwable cause) {
        super(message, cause);
    }
}
