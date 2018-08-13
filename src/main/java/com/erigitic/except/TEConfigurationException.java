package com.erigitic.except;

public class TEConfigurationException extends RuntimeException {

    public TEConfigurationException() {
    }

    public TEConfigurationException(String message) {
        super(message);
    }

    public TEConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TEConfigurationException(Throwable cause) {
        super(cause);
    }

    public TEConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
