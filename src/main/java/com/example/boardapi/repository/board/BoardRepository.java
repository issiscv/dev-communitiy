package com.example.boardapi.repository.board;

import com.example.boardapi.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardCustomRepository {

//    @Query(value = "select b from Board b where b.boardType = :boardType",
//    countQuery = "select count(b) from Board b where b.boardType = :boardType")
//    Page<Board> findAllWithPaging(Pageable pageable, @Param("boardType") BoardType boardType);
}
