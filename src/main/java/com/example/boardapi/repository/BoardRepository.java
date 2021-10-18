package com.example.boardapi.repository;

import com.example.boardapi.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("select b from Board b where b.member.id = :memberId")
    List<Board> findBoardByMember(@Param("memberId") Long memberId);

    @Query(value = "select b from Board b",
    countQuery = "select count(b) from Board b")
    Page<Board> findAllWithPaging(Pageable pageable);
}
