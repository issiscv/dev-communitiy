package com.example.boardapi.exception.exception;

//이미 채택한 경우
public class SelectedCommentExistException extends RuntimeException{

    public SelectedCommentExistException() {
    }

    public SelectedCommentExistException(String message) {
        super(message);
    }

    public SelectedCommentExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public SelectedCommentExistException(Throwable cause) {
        super(cause);
    }

    public SelectedCommentExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
