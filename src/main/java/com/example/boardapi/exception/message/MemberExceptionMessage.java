package com.example.boardapi.exception.message;

public abstract class MemberExceptionMessage {

    public static final String MEMBER_NOT_FOUND = "해당 유저를 찾을 수 없습니다.";
    public static final String DUPLICATE_LOGIN_ID = "중복된 아이디가 존재합니다.";
    public static final String NOT_OWN_MEMBER = "회원 삭제 권한이 없습니다.";
    public static final String INVALID_PASSWORD = "비밀번호가 틀렸습니다.";

}
