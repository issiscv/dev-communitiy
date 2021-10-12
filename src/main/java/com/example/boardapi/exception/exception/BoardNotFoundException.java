package com.example.boardapi.exception.exception;

//찾고자하는 유저가 없는 경우
public class BoardNotFoundException extends RuntimeException{

    public BoardNotFoundException() {
    }

    public BoardNotFoundException(String message) {
        super(message);
    }

    public BoardNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BoardNotFoundException(Throwable cause) {
        super(cause);
    }

    public BoardNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
