package com.example.boardapi.repository.board;

import com.example.boardapi.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface BoardCustomRepository {

    List<Board> findBoardByMember(Long memberId);

    Page<Board> findAllByKeyWordWithPaging(Pageable pageable, String searchCond, String keyWord, String type);

    Page<Board> findByBoardTypeInDateBestBoardsWithPaging(Pageable pageable, LocalDateTime beforeSevenDay);

    Page<Board> findBoardByMemberWithPaging(Pageable pageable, Long memberId);

    void deleteAllByMemberId(Long memberId);
}