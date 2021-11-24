package com.example.boardapi.repository.board;

import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.enumtype.BoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardCustomRepository {

    @Query(value = "select b from Board b where b.boardType = :boardType",
    countQuery = "select count(b) from Board b where b.boardType = :boardType")
    Page<Board> findAllWithPaging(Pageable pageable, @Param("boardType") BoardType boardType);
}
