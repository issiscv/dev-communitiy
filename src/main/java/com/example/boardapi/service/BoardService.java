package com.example.boardapi.service;

import com.example.boardapi.domain.Board;
import com.example.boardapi.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;

    /**
     *  게시글 저장
     */
    @Transactional
    public Board save(Board board) {
        Board saveBoard = boardRepository.save(board);
        return saveBoard;
    }

    /**
     * 단건 조회
     */
    public Board retrieveOne(Long boardId) {
        Board findBoard = boardRepository.findById(boardId).orElse(null);
        return findBoard;
    }

    /**
     * 전체 조회
     */
    public List<Board> retrieveAll() {
        return boardRepository.findAll();
    }
}