package com.example.boardapi.exception.exception;

//찾고자하는 게시글이 없는 경우
public class NotValidUpdateException extends RuntimeException{

    public NotValidUpdateException() {
    }

    public NotValidUpdateException(String message) {
        super(message);
    }

    public NotValidUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotValidUpdateException(Throwable cause) {
        super(cause);
    }

    public NotValidUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
