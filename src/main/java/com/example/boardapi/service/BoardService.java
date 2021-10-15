package com.example.boardapi.service;

import com.example.boardapi.domain.Board;
import com.example.boardapi.dto.board.request.BoardEditRequestDto;
import com.example.boardapi.exception.exception.BoardNotFoundException;
import com.example.boardapi.repository.BoardRepository;
import com.example.boardapi.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final CommentRepository commentRepository;
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
        Board findBoard = boardRepository
                .findById(boardId)
                .orElseThrow(() -> {throw new BoardNotFoundException("해당 게시글이 존재 하지 않습니다.");
        });
        return findBoard;
    }

    /**
     * 전체 조회
     */
    public List<Board> retrieveAll() {
        return boardRepository.findAll();
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public Board editBoard(Long id, BoardEditRequestDto boardEditRequestDto) {
        //retrieveOne 메서드에서 예외 처리 해줌
        Board board = retrieveOne(id);
        
        board.setTitle(boardEditRequestDto.getTitle());
        board.setContent(boardEditRequestDto.getContent());
        return board;
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deleteBoard(Long id) {
        //해당 게시글이 존재하는지 확인하기 위해
        Board board = retrieveOne(id);

        commentRepository.deleteAllByBoardId(id);

        boardRepository.deleteById(id);
    }

    /**
     * 내가 작성한 게시글
     */
    public List<Board> retrieveAllOwnBoard(Long memberId) {
        return boardRepository.findBoardByMember(memberId);
    }
}
