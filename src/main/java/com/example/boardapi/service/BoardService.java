package com.example.boardapi.service;

import com.example.boardapi.dto.board.request.BoardCreateRequestDto;
import com.example.boardapi.dto.board.response.BoardCreateResponseDto;
import com.example.boardapi.dto.board.response.BoardRetrieveDetailResponseDto;
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
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    /**
     *  게시글 저장
     */
    @Transactional
    public BoardCreateResponseDto save(BoardCreateRequestDto boardCreateRequestDto, BoardType type, HttpServletRequest request) {
        List<BoardType> typeList = new ArrayList<>(Arrays.asList(BoardType.TECH, BoardType.QNA, BoardType.FREE));

        if (!typeList.contains(type)) {
            throw new InValidQueryStringException(BoardExceptionMessage.INVALID_QUERYSTRING_TYPE);
        }

        //request 헤더 값을 가져와, 회원 조회 : 누가 작성했는지 알기 위해서
        Member member = jwtTokenProvider.getMember(request);

        //DTO 를 Board 엔티티로 매핑 하고 저장
        Board board = modelMapper.map(boardCreateRequestDto, Board.class);
        board.changeMember(member);
        board.changeBoardType(type);//쿼리스트링에 맞게 엔티티에 매핑
        
        //댓긇 저장
        Board saveBoard = boardRepository.save(board);
        
        //회원 점수 3점 증가
        member.increaseActiveScore(3);

        //응답 DTO
        BoardCreateResponseDto boardCreateResponseDto = modelMapper.map(saveBoard, BoardCreateResponseDto.class);
        boardCreateResponseDto.setAuthor(member.getName());

        return boardCreateResponseDto;
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
    public BoardRetrieveDetailResponseDto retrieveOneAndIncreaseViews(Long boardId) {
        Board findBoard = boardRepository
                .findById(boardId)
                .orElseThrow(() -> {throw new BoardNotFoundException(BoardExceptionMessage.BOARD_NOT_FOUND);
                });

        findBoard.increaseViews();

        //게시판 조회 시 해당 DTO 로 변환
        BoardRetrieveDetailResponseDto boardRetrieveResponseDto = modelMapper.map(findBoard, BoardRetrieveDetailResponseDto.class);
        boardRetrieveResponseDto.setMemberId(findBoard.getMember().getId());
        boardRetrieveResponseDto.setAuthor(findBoard.getMember().getName());
        boardRetrieveResponseDto.setCommentSize(findBoard.getCommentSize());

        return boardRetrieveResponseDto;
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
