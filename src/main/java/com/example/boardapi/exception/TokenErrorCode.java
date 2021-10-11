package com.example.boardapi.exception;

public abstract class TokenErrorCode {

    public static final String NOT_EXIST_USER_TOKEN = "Token`s subject user does not exist, May be that user is deleted";
    public static final String EXPIRED_TOKEN = "X-AUTH-HEADER in the request header is expired. Issue new Token";
    public static final String MALFORMED_TOKEN = "X-AUTH-HEADER in the request header is malformed, Write proper token value";
    public static final String EMPTY_TOKEN = "X-AUTH-HEADER in the request header is empty. Fill the X-AUTH-HEADER`s value";
    public static final String SIGNATURE_NOT_MATCH_TOKEN = "JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.";
}
