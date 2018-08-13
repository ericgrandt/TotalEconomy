package com.erigitic.except;

public class TEConnectionException extends TEConfigurationException {

    public TEConnectionException() {
    }

    public TEConnectionException(String message) {
        super(message);
    }

    public TEConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TEConnectionException(Throwable cause) {
        super(cause);
    }

    public TEConnectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
