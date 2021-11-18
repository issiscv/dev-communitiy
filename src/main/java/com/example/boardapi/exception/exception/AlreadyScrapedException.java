package com.example.boardapi.exception.exception;

//이미 스크랩한 경우
public class AlreadyScrapedException extends RuntimeException{

    public AlreadyScrapedException() {
    }

    public AlreadyScrapedException(String message) {
        super(message);
    }

    public AlreadyScrapedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyScrapedException(Throwable cause) {
        super(cause);
    }

    public AlreadyScrapedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
