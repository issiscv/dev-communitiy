package com.example.boardapi.exception.exception;

//댓글, 게시글에 중복으로 좋아요 누를때
public class DuplicatedLikeException extends RuntimeException{

    public DuplicatedLikeException() {
    }

    public DuplicatedLikeException(String message) {
        super(message);
    }

    public DuplicatedLikeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicatedLikeException(Throwable cause) {
        super(cause);
    }

    public DuplicatedLikeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
