package com.example.boardapi.exception.exception;

//올바르지 않은 쿼리 스트링
public class InValidQueryStringException extends RuntimeException {
    public InValidQueryStringException() {
        super();
    }

    public InValidQueryStringException(String message) {
        super(message);
    }

    public InValidQueryStringException(String message, Throwable cause) {
        super(message, cause);
    }

    public InValidQueryStringException(Throwable cause) {
        super(cause);
    }

    protected InValidQueryStringException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
