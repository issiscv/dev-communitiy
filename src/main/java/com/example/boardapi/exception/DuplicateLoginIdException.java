package com.example.boardapi.exception;

public class DuplicateLoginIdException extends RuntimeException {
    public DuplicateLoginIdException() {
        super();
    }

    public DuplicateLoginIdException(String message) {
        super(message);
    }

    public DuplicateLoginIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateLoginIdException(Throwable cause) {
        super(cause);
    }

    protected DuplicateLoginIdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
