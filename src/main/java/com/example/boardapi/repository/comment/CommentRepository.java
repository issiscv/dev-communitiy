package com.example.boardapi.repository.comment;

import com.example.boardapi.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {

//    @Query("select c from Comment c join fetch c.member m where c.id = : commentId")
//    Optional<Comment> findByIdWithFetch(@Param("commentId") Long commentId);
//
//    @Query("select c from Comment c join fetch c.member m where c.board.id = :boardId")
//    List<Comment> findAllByBoardId(@Param("boardId") Long boardId);
//
//    @Modifying(clearAutomatically = true)
//    @Query("delete from Comment c where c.board.id = :boardId")
//    void deleteAllByBoardId(@Param("boardId") Long boardId);
//
//    @Query("select c from Comment c join fetch c.board b join fetch c.member m where c.member.id = :memberId")
//    List<Comment> findAllByMemberId(@Param("memberId") Long memberId);
//
//    @Modifying(clearAutomatically = true)
//    @Query("delete from Comment c where c.member.id = :memberId")
//    void deleteAllByMemberId(@Param("memberId") Long memberId);
}
