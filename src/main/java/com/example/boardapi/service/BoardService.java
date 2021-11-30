package com.example.boardapi.service;

import com.example.boardapi.dto.board.request.BoardCreateRequestDto;
import com.example.boardapi.dto.board.request.BoardEditRequestDto;
import com.example.boardapi.dto.board.response.*;
import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.Member;
import com.example.boardapi.entity.enumtype.BoardType;
import com.example.boardapi.entity.enumtype.SortType;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    public BoardCreateResponseDto save(BoardCreateRequestDto boardCreateRequestDto, BoardType boardType, String token) {
        List<BoardType> typeList = new ArrayList<>(Arrays.asList(BoardType.TECH, BoardType.QNA, BoardType.FREE));

        if (!typeList.contains(boardType)) {
            throw new InValidQueryStringException(BoardExceptionMessage.INVALID_QUERYSTRING_TYPE);
        }

        //request 헤더 값을 가져와, 회원 조회 : 누가 작성했는지 알기 위해서
        Member member = jwtTokenProvider.getMember(token);

        //DTO 를 Board 엔티티로 매핑 하고 저장
        Board board = modelMapper.map(boardCreateRequestDto, Board.class);
        board.changeMember(member);
        board.changeBoardType(boardType);//쿼리스트링에 맞게 엔티티에 매핑
        
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
    public BoardRetrieveAllPagingResponseDto retrieveAllWithPagingByType(int page, BoardType boardType, SortType sortType) {
        List<SortType> sortList = new ArrayList<>(Arrays.asList(SortType.CREATEDATE, SortType.LIKES, SortType.COMMENTSIZE, SortType.VIEWS));
        List<BoardType> typeList = new ArrayList<>(Arrays.asList(BoardType.TECH, BoardType.QNA, BoardType.FREE));
        
        //정렬 조건에 맞지 않으면 에로
        if (!sortList.contains(sortType)) {
            throw new InValidQueryStringException(BoardExceptionMessage.INVALID_QUERYSTRING_SORT);
        }
        
        //게시글 타입에 맞지 않으면 에로
        if (!typeList.contains(boardType)) {
            throw new InValidQueryStringException(BoardExceptionMessage.INVALID_QUERYSTRING_TYPE);
        }

        //페이징 기준
        PageRequest pageRequest = PageRequest.of(page-1, 15);
        Page<Board> boardPage = boardRepository.findAllWithPaging(pageRequest, boardType, sortType);

        //총 게시글 수
        long totalElements = boardPage.getTotalElements();
        //총 페이지 수
        int totalPages = boardPage.getTotalPages();
        //해당 페이지의 컨텐트들
        List<Board> content = boardPage.getContent();

        List<BoardRetrieveResponseDto> boardRetrieveOneResponseDtoList = content.stream().map(board -> {
                    BoardRetrieveResponseDto boardRetrieveOneResponseDto = modelMapper.map(board, BoardRetrieveResponseDto.class);
                    boardRetrieveOneResponseDto.setAuthor(board.getMember().getName());
                    return boardRetrieveOneResponseDto;
                }
        ).collect(Collectors.toList());

        BoardRetrieveAllPagingResponseDto boardRetrieveAllPagingResponseDto =
                new BoardRetrieveAllPagingResponseDto(page, totalPages, (int)totalElements, boardRetrieveOneResponseDtoList);

        return boardRetrieveAllPagingResponseDto;
    }

    //일, 주, 월 이내의 best 게시글 10개 조회
    public BoardRetrieveAllByWeekResponseDto retrieveByTypeAndWeeklyBestBoardsWithPaging() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        
        //7일 전 날짜의 0시 0분 0초로 초기화 -> 즉, 날짜가 변경될때만 반영
        LocalDateTime beforeDate = LocalDateTime.of(LocalDate.now().minusDays(7), LocalTime.of(0,0,0));

        Page<Board> allWithPaging = boardRepository.findBestBoardsBySevenDaysWithPaging(pageRequest, beforeDate);

        List<Board> content = allWithPaging.getContent();

        List<BoardRetrieveResponseDto> boardRetrieveOneResponseDtoList = content.stream().filter(board -> board.getLikes() > 0).map(board -> {
                    BoardRetrieveResponseDto boardRetrieveOneResponseDto = modelMapper.map(board, BoardRetrieveResponseDto.class);
                    boardRetrieveOneResponseDto.setAuthor(board.getMember().getName());
                    return boardRetrieveOneResponseDto;
                }
        ).collect(Collectors.toList());

        BoardRetrieveAllByWeekResponseDto boardRetrieveAllByDateResponseDto = new BoardRetrieveAllByWeekResponseDto(boardRetrieveOneResponseDtoList);

        return boardRetrieveAllByDateResponseDto;
    }

    public Page<Board> retrieveAllOwnBoardWithPaging(Pageable page, Long memberId) {
        return boardRepository.findBoardByMemberWithPaging(page, memberId);
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public BoardEditResponseDto editBoard(Long id, BoardEditRequestDto boardEditRequestDto, String token) {

        Member member = jwtTokenProvider.getMember(token);

        //retrieveOne 메서드에서 예외 처리 해줌
        Board board = retrieveOne(id);

        if (board.getMember().getId() != member.getId()) {
            throw new NotOwnBoardException(BoardExceptionMessage.NOT_OWN_BOARD);
        }
        
        board.changeTitle(boardEditRequestDto.getTitle());
        board.changeContent(boardEditRequestDto.getContent());

        BoardEditResponseDto boardEditResponseDto = modelMapper.map(board, BoardEditResponseDto.class);
        boardEditResponseDto.setAuthor(board.getMember().getName());
        return boardEditResponseDto;
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deleteBoard(Long boardId, String token) {

        Board board = retrieveOne(boardId);

        Member member = jwtTokenProvider.getMember(token);

        if (board.getMember().getId() != member.getId()) {
            throw new NotOwnBoardException(BoardExceptionMessage.NOT_OWN_BOARD);
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
    public void updateBoardLike(Long boardId, String token) {
        Member member = jwtTokenProvider.getMember(token);

        if (member.getLikeId().contains(boardId)) {
            throw new DuplicatedLikeException(BoardExceptionMessage.DUPLICATED_LIKE);
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

    public BoardRetrieveAllPagingResponseDto retrieveAllWithPagingByKeyWord(int page, String searchCond, String keyWord, BoardType type) {
        List<BoardType> typeList = new ArrayList(Arrays.asList(BoardType.FREE, BoardType.QNA, BoardType.TECH));
        List<String> searchCondList = new ArrayList(Arrays.asList("title", "content", "all"));

        //페이징 기준
        PageRequest pageRequest = PageRequest.of(page-1, 15);

        if (!typeList.contains(type)) {
            throw new InValidQueryStringException(BoardExceptionMessage.INVALID_QUERYSTRING_TYPE);
        }
        if (!searchCondList.contains(searchCond)) {
            throw new InValidQueryStringException(BoardExceptionMessage.INVALID_QUERYSTRING_SEACHCOND);
        }

        Page<Board> boardPage = boardRepository.findAllByKeyWordWithPaging(pageRequest, searchCond, keyWord, type);

        long totalElements = boardPage.getTotalElements();

        //총 페이지 수
        int totalPages = boardPage.getTotalPages();
        //해당 페이지의 컨텐트들
        List<Board> content = boardPage.getContent();

        List<BoardRetrieveResponseDto> boardRetrieveOneResponseDtoList = content.stream().map(board -> {
                    BoardRetrieveResponseDto boardRetrieveOneResponseDto = modelMapper.map(board, BoardRetrieveResponseDto.class);
                    boardRetrieveOneResponseDto.setAuthor(board.getMember().getName());
                    return boardRetrieveOneResponseDto;
                }
        ).collect(Collectors.toList());

        BoardRetrieveAllPagingResponseDto boardRetrieveAllPagingResponseDto =
                new BoardRetrieveAllPagingResponseDto(page, totalPages, (int)totalElements, boardRetrieveOneResponseDtoList);

        return boardRetrieveAllPagingResponseDto;
    }
}
