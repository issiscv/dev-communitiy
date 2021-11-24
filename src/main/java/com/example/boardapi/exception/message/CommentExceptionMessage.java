package com.example.boardapi.exception.message;

public abstract class CommentExceptionMessage {

    public static final String COMMENT_NOT_FOUND = "해당 댓글이 없습니다.";
    public static final String INVALID_COMMENT_UPDATE = "채택된 댓글은 수정, 삭제할 수 없습니다.";
    public static final String INVALID_SELECTION = "이미 댓글을 채택하셧습니다.";
}
