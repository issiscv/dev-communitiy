package com.example.boardapi.exception;

//수정, 삭제 하고자 하는 글이 나의 게시글이 아닐 때
public class NotOwnBoardException extends RuntimeException{

    public NotOwnBoardException() {
    }

    public NotOwnBoardException(String message) {
        super(message);
    }

    public NotOwnBoardException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotOwnBoardException(Throwable cause) {
        super(cause);
    }

    public NotOwnBoardException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
