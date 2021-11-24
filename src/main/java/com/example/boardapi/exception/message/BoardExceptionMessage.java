package com.example.boardapi.exception.message;

public abstract class BoardExceptionMessage {

    public static final String INVALID_QUERYSTRING_TYPE = "type 의 value 로 free, qna, tech 의 쿼리스트링만 입력 가능합니다.";
    public static final String INVALID_QUERYSTRING_SORT = "sort 의 value 로 createdDate, likes, commentSize, views 의 쿼리스트링만 입력해주세요.";
    public static final String INVALID_QUERYSTRING_SEACHCOND = "searchCond 의 쿼리스트링으로 title, content, all 만 입력하세요.";
    public static final String BOARD_NOT_FOUND = "해당 게시글이 존재 하지 않습니다.";
    public static final String BOARD_NOT_DELETE = "댓글이 있는 게시글은 삭제할 수 없습니다.";
}
