package com.example.boardapi.service;

import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.Member;
import com.example.boardapi.entity.enumtype.BoardType;
import com.example.boardapi.dto.board.request.BoardEditRequestDto;
import com.example.boardapi.exception.exception.BoardNotDeletedException;
import com.example.boardapi.exception.exception.BoardNotFoundException;
import com.example.boardapi.exception.exception.NotValidQueryStringException;
import com.example.boardapi.repository.BoardRepository;
import com.example.boardapi.repository.CommentRepository;
import com.example.boardapi.repository.ScrapRepository;
import com.example.boardapi.repository.ScrapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BoardService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final ScrapRepository scrapRepository;

    /**
     *  게시글 저장
     */
    @Transactional
    public Board save(Board board, Member member, String type) {

        //쿼리스트링에 맞게 엔티티에 매핑
        if (type.equals("free")) {
            board.changeBoardType(BoardType.FREE);
        } else if (type.equals("qna")) {
            board.changeBoardType(BoardType.QNA);
        } else if (type.equals("tech")) {
            board.changeBoardType(BoardType.TECH);
        } else {
            throw new NotValidQueryStringException("free, qna, tech 의 쿼리스트링만 입력 가능합니다.");
        }

        Board saveBoard = boardRepository.save(board);
        member.increaseActiveScore(3);
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
     * 단건 조회 시 조회 수도 증가
     */
    @Transactional
    public Board retrieveOneAndIncreaseViews(Long boardId) {
        Board findBoard = boardRepository
                .findById(boardId)
                .orElseThrow(() -> {throw new BoardNotFoundException("해당 게시글이 존재 하지 않습니다.");
                });

        findBoard.increaseViews();

        return findBoard;
    }

    /**
     * 전체 조회
     */
    public List<Board> retrieveAll() {
        return boardRepository.findAll();
    }

    public Page<Board> retrieveAllWithPagingByType(Pageable pageable, String type, String sort) {
        Page<Board> allWithPaging = null;

        if (!(sort.equals("createdDate") || sort.equals("likes") || sort.equals("commentSize") || sort.equals("views"))) {
            throw new NotValidQueryStringException("sort의 value로 createdDate, likes, commentSize, views의 퀄리 스트링만 입력해주세요.");
        }

        if (type.equals("free")) {
            allWithPaging = boardRepository.findAllWithPaging(pageable, BoardType.FREE);
        } else if (type.equals("qna")) {
            allWithPaging = boardRepository.findAllWithPaging(pageable, BoardType.QNA);
        } else if (type.equals("tech")) {
            allWithPaging = boardRepository.findAllWithPaging(pageable, BoardType.TECH);
        } else {
            throw new NotValidQueryStringException("free, qna, tech 의 쿼리스트링만 입력 가능합니다.");
        }
        return allWithPaging;
    }

    //일, 주, 월 이내의 best 게시글 10개 조회
    public Page<Board> retrieveByTypeAndWeeklyBestBoardsWithPaging(Pageable pageable) {
        LocalDateTime beforeDate = LocalDateTime.of(LocalDate.now().minusDays(7), LocalTime.of(0,0,0));

        //어제 날짜의 0시 0분 0초로 초기화 -> 즉, 날짜가 변경될때만 반영

        Page<Board> allWithPaging = boardRepository.findByBoardTypeInDateBestBoardsWithPaging(pageable, beforeDate);

        return allWithPaging;
    }

    public Page<Board> retrieveAllOwnBoardWithPaging(Pageable page, Long memberId) {
        return boardRepository.findBoardByMemberWithPaging(page, memberId);
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public Board editBoard(Long id, BoardEditRequestDto boardEditRequestDto) {
        //retrieveOne 메서드에서 예외 처리 해줌
        Board board = retrieveOne(id);
        
        board.changeTitle(boardEditRequestDto.getTitle());
        board.changeContent(boardEditRequestDto.getContent());
        return board;
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deleteBoard(Long boardId) {

        Board board = retrieveOne(boardId);
        if (board.getCommentSize() > 1) {
            throw new BoardNotDeletedException("댓글이 있는 게시글은 삭제할 수 없습니다.");
        }

        scrapRepository.deleteByBoardId(boardId);
        boardRepository.deleteByBoardId(boardId);
    }

    /**
     * 내가 작성한 게시글
     */
    public List<Board> retrieveAllOwnBoard(Long memberId) {
        return boardRepository.findBoardByMember(memberId);
    }

    @Transactional
    public void updateBoardLike(Long boardId) {
        Board board = retrieveOne(boardId);
        int like = board.getLikes();
        board.changeLike(++like);
    }

    @Transactional
    public void deleteAllOwnBoard(Long memberId) {
        boardRepository.deleteAllByMemberId(memberId);
    }

    public Page<Board> retrieveAllWithPagingByKeyWord(PageRequest pageRequest, String keyWord) {
        return boardRepository.findAllByKeyWordWithPaging(pageRequest, keyWord);
    }
}
