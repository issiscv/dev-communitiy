package com.example.boardapi.exception;

//자신의 알림이 아닌걸 조회할 때
public class NotOwnNoticeException extends RuntimeException{

    public NotOwnNoticeException() {
    }

    public NotOwnNoticeException(String message) {
        super(message);
    }

    public NotOwnNoticeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotOwnNoticeException(Throwable cause) {
        super(cause);
    }

    public NotOwnNoticeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
