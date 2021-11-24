package com.example.boardapi.exception;

//이미 스크랩한 경우
public class BoardNotDeletedException extends RuntimeException{

    public BoardNotDeletedException() {
    }

    public BoardNotDeletedException(String message) {
        super(message);
    }

    public BoardNotDeletedException(String message, Throwable cause) {
        super(message, cause);
    }

    public BoardNotDeletedException(Throwable cause) {
        super(cause);
    }

    public BoardNotDeletedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
