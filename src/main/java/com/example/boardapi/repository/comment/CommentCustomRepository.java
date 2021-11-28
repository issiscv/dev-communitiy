package com.example.boardapi.repository.comment;

import com.example.boardapi.entity.Comment;

import java.util.List;

public interface CommentCustomRepository {

    List<Comment> findAllByBoardId(Long boardId);

    List<Comment> findAllByBoardIdFetchJoinWithMember(Long boardId);

    void deleteAllByBoardId(Long boardId);

    List<Comment> findAllByMemberId(Long memberId);

    void deleteAllByMemberId(Long memberId);
}
