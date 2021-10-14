package com.example.boardapi.repository;

import com.example.boardapi.domain.Board;
import com.example.boardapi.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("select b from Board b where b.member.id = :memberId")
    List<Board> findBoardByMember(@Param("memberId") Long memberId);
}
