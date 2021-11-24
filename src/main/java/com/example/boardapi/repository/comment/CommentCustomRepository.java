package com.example.boardapi.repository.comment;

import com.example.boardapi.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentCustomRepository {

    List<Comment> findAllByBoardId(Long boardId);

    void deleteAllByBoardId(Long boardId);

    List<Comment> findAllByMemberId(Long memberId);

    void deleteAllByMemberId(Long memberId);
}
