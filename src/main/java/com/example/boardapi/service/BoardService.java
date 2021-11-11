package com.example.boardapi.service;

import com.example.boardapi.domain.Board;
import com.example.boardapi.domain.Member;
import com.example.boardapi.domain.enumtype.BoardType;
import com.example.boardapi.dto.board.request.BoardEditRequestDto;
import com.example.boardapi.exception.exception.BoardNotFoundException;
import com.example.boardapi.exception.exception.NotValidQueryStringException;
import com.example.boardapi.exception.exception.UserNotFoundException;
import com.example.boardapi.repository.BoardRepository;
import com.example.boardapi.repository.CommentRepository;
import com.example.boardapi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final EntityManager em;

    /**
     *  게시글 저장
     */
    @Transactional
    public Board save(Board board, Member member, String type) {

        //쿼리스트링에 맞게 엔티티에 매핑
        if (type.equals("free")) {
            board.setBoardType(BoardType.FREE);
        } else if (type.equals("qna")) {
            board.setBoardType(BoardType.QNA);
        } else if (type.equals("tech")) {
            board.setBoardType(BoardType.TECH);
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

    public Page<Board> retrieveAllWithPagingByType(Pageable pageable, String type) {
        Page<Board> allWithPaging = null;
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

    //일, 주, 월 이내의 best 게시글 5개 조회
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
        
        board.setTitle(boardEditRequestDto.getTitle());
        board.setContent(boardEditRequestDto.getContent());
        return board;
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deleteBoard(Member member, Long id) {
        member.decreaseActiveScore(3);

        em.flush();
        em.clear();
        //벌크 연산 시 영속성 컨텍스트를 무시하고 데이터베이스에 직접 쿼리를 날린다.
        //벌크 연산을 수행 후 영속성 컨텍스트를 비워서, 변경이 일어나지 않는다.
        commentRepository.deleteAllByBoardId(id);
        boardRepository.deleteById(id);
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
        board.setLikes(++like);
    }

    public void deleteAllOwnBoard(Long memberId) {
        boardRepository.deleteAllByMemberId(memberId);
    }
}
