package com.example.boardapi.repository;

import com.example.boardapi.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c join fetch c.member m where c.board.id = :boardId")
    List<Comment> findAllByBoardId(@Param("boardId") Long boardId);

    @Modifying(clearAutomatically = true)
    @Query("delete from Comment c where c.board.id = :boardId")
    void deleteAllByBoardId(@Param("boardId") Long boardId);

    @Query("select c from Comment c join fetch c.board b join fetch c.member m where c.member.id = :memberId")
    List<Comment> findAllByMemberId(@Param("memberId") Long memberId);

    @Modifying(clearAutomatically = true)
    @Query("delete from Comment c where c.member.id = :memberId")
    void deleteAllByMemberId(@Param("memberId") Long memberId);
}
