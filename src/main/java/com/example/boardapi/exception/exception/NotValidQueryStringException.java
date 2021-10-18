package com.example.boardapi.exception.exception;

//올바르지 않은 쿼리 스트링
public class NotValidQueryStringException extends RuntimeException {
    public NotValidQueryStringException() {
        super();
    }

    public NotValidQueryStringException(String message) {
        super(message);
    }

    public NotValidQueryStringException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotValidQueryStringException(Throwable cause) {
        super(cause);
    }

    protected NotValidQueryStringException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
