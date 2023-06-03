package org.bell;

public class EulaException extends Exception {
    public EulaException(String message) {
        super(message);
    }

    public EulaException(String message, Throwable cause) {
        super(message, cause);
    }

    public EulaException(Throwable cause) {
        super(cause);
    }

    public EulaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public EulaException() {
    }
}
