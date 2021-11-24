package com.example.boardapi.exception;

//이미 채택한 경우
public class InvalidSelectionException extends RuntimeException{

    public InvalidSelectionException() {
    }

    public InvalidSelectionException(String message) {
        super(message);
    }

    public InvalidSelectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSelectionException(Throwable cause) {
        super(cause);
    }

    public InvalidSelectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
