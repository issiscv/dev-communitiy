package com.example.boardapi.exception;

//입력 글자가 작을때
public class ShortInputException extends RuntimeException{

    public ShortInputException() {
    }

    public ShortInputException(String message) {
        super(message);
    }

    public ShortInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShortInputException(Throwable cause) {
        super(cause);
    }

    public ShortInputException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
