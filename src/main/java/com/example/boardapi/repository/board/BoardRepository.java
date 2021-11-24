package com.example.boardapi.repository.board;

import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.enumtype.BoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardCustomRepository {
//
//    @Query("select b from Board b where b.member.id = :memberId")
//    List<Board> findBoardByMember(@Param("memberId") Long memberId);
//
    @Query(value = "select b from Board b where b.boardType = :boardType",
    countQuery = "select count(b) from Board b where b.boardType = :boardType")
    Page<Board> findAllWithPaging(Pageable pageable, @Param("boardType") BoardType boardType);

    @Query("delete from Board b where b.id = :boardId")
    @Modifying(clearAutomatically = true)
    void deleteByBoardId(@Param("boardId") Long boardId);

//
//    @Query(value = "select b from Board b where b.createdDate >= :beforeSevenDay",
//    countQuery = "select count(b) from Board b where b.createdDate >= :beforeSevenDay")
//    Page<Board> findByBoardTypeInDateBestBoardsWithPaging(Pageable pageable, @Param("beforeSevenDay")LocalDateTime beforeSevenDay);
//
//    @Query(value = "select b from Board b where b.member.id = :memberId",
//            countQuery = "select count(b) from Board b where b.member.id = :memberId")
//    Page<Board> findBoardByMemberWithPaging(Pageable pageable, @Param("memberId") Long memberId);
//
//    @Modifying(clearAutomatically = true)
//    @Query("delete from Board b where b.member.id = :memberId")
//    void deleteAllByMemberId(@Param("memberId") Long memberId);
}
