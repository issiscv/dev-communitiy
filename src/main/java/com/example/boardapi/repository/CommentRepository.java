package com.example.boardapi.repository;

import com.example.boardapi.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c join fetch c.member m where c.board.id = :boardId")
    List<Comment> findAllByBoardId(@Param("boardId") Long boardId);
}
