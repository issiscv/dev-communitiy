package com.example.boardapi.exception;

public class EmptyTokenException extends RuntimeException {

    public EmptyTokenException() {
    }

    public EmptyTokenException(String message) {
        super(message);
    }

    public EmptyTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyTokenException(Throwable cause) {
        super(cause);
    }

    public EmptyTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
