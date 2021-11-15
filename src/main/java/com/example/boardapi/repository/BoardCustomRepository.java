package com.example.boardapi.repository;

import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.enumtype.BoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BoardCustomRepository {

    List<Board> findBoardByMember(Long memberId);

//    Page<Board> findAllWithPaging(Pageable pageable, BoardType boardType);

    Page<Board> findByBoardTypeInDateBestBoardsWithPaging(Pageable pageable, LocalDateTime beforeSevenDay);

    Page<Board> findBoardByMemberWithPaging(Pageable pageable, Long memberId);

    void deleteAllByMemberId(Long memberId);
}
