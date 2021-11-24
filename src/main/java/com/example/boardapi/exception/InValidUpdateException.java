package com.example.boardapi.exception;

//찾고자하는 게시글이 없는 경우
public class InValidUpdateException extends RuntimeException{

    public InValidUpdateException() {
    }

    public InValidUpdateException(String message) {
        super(message);
    }

    public InValidUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public InValidUpdateException(Throwable cause) {
        super(cause);
    }

    public InValidUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
