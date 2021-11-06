package com.example.boardapi.exception.exception;

//수정, 삭제, 프로필 조회 시 나의 회원 정보가 아닐 때
public class NotOwnMemberException extends RuntimeException{

    public NotOwnMemberException() {
    }

    public NotOwnMemberException(String message) {
        super(message);
    }

    public NotOwnMemberException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotOwnMemberException(Throwable cause) {
        super(cause);
    }

    public NotOwnMemberException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
