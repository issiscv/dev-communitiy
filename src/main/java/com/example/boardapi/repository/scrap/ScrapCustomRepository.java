package com.example.boardapi.repository.scrap;

import com.example.boardapi.entity.Scrap;

import java.util.List;

public interface ScrapCustomRepository {
    List<Scrap> findByMemberId(Long memberId);

    void deleteByBoardId(Long boardId);

    void deleteByMemberId(Long memberId);
}
