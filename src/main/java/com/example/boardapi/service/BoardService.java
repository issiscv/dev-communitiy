package com.example.boardapi.service;

import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.Member;
import com.example.boardapi.entity.enumtype.BoardType;
import com.example.boardapi.dto.board.request.BoardEditRequestDto;
import com.example.boardapi.exception.*;
import com.example.boardapi.exception.message.BoardExceptionMessage;
import com.example.boardapi.repository.board.BoardRepository;
import com.example.boardapi.repository.comment.CommentRepository;
import com.example.boardapi.repository.scrap.ScrapRepository;
import com.example.boardapi.security.JWT.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BoardService {

    private final JwtTokenProvider jwtTokenProvider;
    private final BoardRepository boardRepository;
    private final ScrapRepository scrapRepository;
    private final CommentRepository commentRepository;

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
            throw new InValidQueryStringException(BoardExceptionMessage.INVALID_QUERYSTRING_TYPE);
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
                .orElseThrow(() -> {throw new BoardNotFoundException(BoardExceptionMessage.BOARD_NOT_FOUND);
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
                .orElseThrow(() -> {throw new BoardNotFoundException(BoardExceptionMessage.BOARD_NOT_FOUND);
                });

        findBoard.increaseViews();

        return findBoard;
    }

    /**
     * 전체 조회
     */
    public Page<Board> retrieveAllWithPagingByType(Pageable pageable, String type, String sort) {
        List<String> sortList = new ArrayList<>(Arrays.asList("createdDate", "likes", "commentSize", "views"));
        List<String> typeList = new ArrayList<>(Arrays.asList("free", "qna", "tech"));


        if (!sortList.contains(sort)) {
            throw new InValidQueryStringException(BoardExceptionMessage.INVALID_QUERYSTRING_SORT);
        }

        if (!typeList.contains(type)) {
            throw new InValidQueryStringException(BoardExceptionMessage.INVALID_QUERYSTRING_TYPE);
        }

        Page<Board> allWithPaging = boardRepository.findAllWithPaging(pageable, type, sort);

        return allWithPaging;
    }

    //일, 주, 월 이내의 best 게시글 10개 조회
    public Page<Board> retrieveByTypeAndWeeklyBestBoardsWithPaging(Pageable pageable) {
        LocalDateTime beforeDate = LocalDateTime.of(LocalDate.now().minusDays(7), LocalTime.of(0,0,0));

        //어제 날짜의 0시 0분 0초로 초기화 -> 즉, 날짜가 변경될때만 반영

        Page<Board> allWithPaging = boardRepository.findBestBoardsBySevenDaysWithPaging(pageable, beforeDate);

        return allWithPaging;
    }

    public Page<Board> retrieveAllOwnBoardWithPaging(Pageable page, Long memberId) {
        return boardRepository.findBoardByMemberWithPaging(page, memberId);
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public Board editBoard(Long id, BoardEditRequestDto boardEditRequestDto, HttpServletRequest request) {

        Member member = jwtTokenProvider.getMember(request);

        //retrieveOne 메서드에서 예외 처리 해줌
        Board board = retrieveOne(id);

        if (board.getMember().getId() != member.getId()) {
            throw new NotOwnBoardException("게시글의 권한이 없습니다.");
        }
        
        board.changeTitle(boardEditRequestDto.getTitle());
        board.changeContent(boardEditRequestDto.getContent());
        return board;
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deleteBoard(Long boardId, HttpServletRequest request) {

        Board board = retrieveOne(boardId);

        Member member = jwtTokenProvider.getMember(request);

        if (board.getMember().getId() != member.getId()) {
            throw new NotOwnBoardException("게시글의 권한이 없습니다.");
        }

        if (board.getCommentSize() >= 1) {
            throw new BoardNotDeletedException(BoardExceptionMessage.BOARD_NOT_DELETE);
        }


        scrapRepository.deleteByBoardId(boardId);
        commentRepository.deleteAllByBoardId(boardId);
        boardRepository.deleteByBoardId(boardId);
    }

    /**
     * 내가 작성한 게시글
     */
    public List<Board> retrieveAllOwnBoard(Long memberId) {
        return boardRepository.findBoardByMember(memberId);
    }

    @Transactional
    public void updateBoardLike(Long boardId, HttpServletRequest request) {
        Member member = jwtTokenProvider.getMember(request);

        if (member.getLikeId().contains(boardId)) {
            throw new DuplicatedLikeException("이미 좋아요를 눌렀습니다.");
        }

        member.getLikeId().add(boardId);

        Board board = retrieveOne(boardId);

        int like = board.getLikes();
        board.changeLike(++like);
    }



    @Transactional
    public void deleteAllOwnBoard(Long memberId) {
        boardRepository.deleteAllByMemberId(memberId);
    }

    public Page<Board> retrieveAllWithPagingByKeyWord(PageRequest pageRequest, String searchCond, String keyWord, String type) {
        List<String> typeList = new ArrayList(Arrays.asList("free", "qna", "tech"));
        List<String> searchCondList = new ArrayList(Arrays.asList("title", "content", "all"));
        if (!typeList.contains(type)) {
            throw new InValidQueryStringException(BoardExceptionMessage.INVALID_QUERYSTRING_TYPE);
        }
        if (!searchCondList.contains(searchCond)) {
            throw new InValidQueryStringException(BoardExceptionMessage.INVALID_QUERYSTRING_SEACHCOND);
        }
        return boardRepository.findAllByKeyWordWithPaging(pageRequest, searchCond, keyWord, type);
    }
}
