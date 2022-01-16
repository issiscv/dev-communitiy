package com.example.boardapi.service;

import com.example.boardapi.dto.comment.request.CommentCreateRequestDto;
import com.example.boardapi.dto.comment.request.CommentEditRequestDto;
import com.example.boardapi.dto.comment.response.CommentCreateResponseDto;
import com.example.boardapi.dto.comment.response.CommentEditResponseDto;
import com.example.boardapi.dto.comment.response.CommentRetrieveResponseDto;
import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.Comment;
import com.example.boardapi.entity.Member;
import com.example.boardapi.exception.*;
import com.example.boardapi.exception.message.BoardExceptionMessage;
import com.example.boardapi.exception.message.CommentExceptionMessage;
import com.example.boardapi.config.jwt.JwtTokenProvider;
import com.example.boardapi.repository.board.BoardRepository;
import com.example.boardapi.repository.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CommentService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ModelMapper modelMapper;
    /**
     * 댓글 저장
     */
    @Transactional
    public CommentCreateResponseDto save(Long boardId, CommentCreateRequestDto commentCreateRequestDto, String token) {
        //글쓴이의 정보(토큰의 정보)
        Member member = jwtTokenProvider.getMember(token);

        //게시글 엔티티 조회
        Board board = boardRepository
                .findById(boardId)
                .orElseThrow(() -> {throw new BoardNotFoundException(BoardExceptionMessage.BOARD_NOT_FOUND);
                });


        //DTO 를 변환 엔티티로 변환
        Comment comment = modelMapper.map(commentCreateRequestDto, Comment.class);
        comment.changeBoard(board);
        comment.changeMember(member);

        Comment saveComment = commentRepository.save(comment);

        //엔티티를 DTO로 변환
        CommentCreateResponseDto commentResponseDto = modelMapper.map(saveComment, CommentCreateResponseDto.class);
        commentResponseDto.setAuthor(member.getName());//작성자
        commentResponseDto.setId(saveComment.getId());//댓글 기본키
        commentResponseDto.setBoardId(board.getId());//게시글 기본키
        commentResponseDto.setMemberId(member.getId());
        commentResponseDto.setSelected(commentResponseDto.isSelected());

        member.increaseActiveScore(2);
        board.increaseComments();

        return commentResponseDto;
    }

    /**
     * 단건 조회
     */
    public Comment retrieveOne(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> {
            throw new CommentNotFoundException(CommentExceptionMessage.COMMENT_NOT_FOUND);
        }
        );
    }

    public CommentRetrieveResponseDto retrieveOneWithDto(Long commentId) {
        Comment comment = retrieveOne(commentId);

        CommentRetrieveResponseDto commentRetrieveResponseDto = modelMapper.map(comment, CommentRetrieveResponseDto.class);
        commentRetrieveResponseDto.setAuthor(comment.getMember().getName());
        commentRetrieveResponseDto.setMemberId(comment.getMember().getId());
        commentRetrieveResponseDto.setBoardId(comment.getBoard().getId());

        return commentRetrieveResponseDto;
    }

    /**
     * 전체 조회
     */
    public List<Comment> retrieveAll() {
        return commentRepository.findAll();
    }

    /**
     * 특정 게시글의 댓글
     */
    public List<CommentRetrieveResponseDto> retrieveAllByBoardId(Long boardId) {
        List<Comment> comments = commentRepository.findAllByBoardIdFetchJoinWithMember(boardId);

        List<CommentRetrieveResponseDto> commentResponseDtoList = new ArrayList<>();

        //조회한 댓글 엔티티를 DTO 로 변환
        for (Comment comment : comments) {

            CommentRetrieveResponseDto commentRetrieveResponseDto = CommentRetrieveResponseDto.builder()
                    .id(comment.getId())
                    .memberId(comment.getMember().getId())
                    .boardId(boardId)
                    .author(comment.getMember().getName())
                    .content(comment.getContent())
                    .createdDate(comment.getCreatedDate())
                    .lastModifiedDate(comment.getLastModifiedDate())
                    .likes(comment.getLikes())
                    .isSelected(comment.isSelected())
                    .build();

            commentResponseDtoList.add(commentRetrieveResponseDto);
        }

        return commentResponseDtoList;
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public CommentEditResponseDto editComment(Long commentId, Long boardId, CommentEditRequestDto commentEditRequestDto, String token) {

        Member member = jwtTokenProvider.getMember(token);

        //게시글이 존재하는지 검사
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> {
                    throw new BoardNotFoundException(BoardExceptionMessage.BOARD_NOT_FOUND);
                }
        );

        Comment comment = retrieveOne(commentId);

        if (comment.getMember().getId() != member.getId()) {
            throw new NotOwnBoardException("게시글의 권한이 없습니다.");
        }

        if (comment.isSelected()) {
            throw new InValidUpdateException(CommentExceptionMessage.INVALID_COMMENT_UPDATE);
        }
        comment.changeContent(commentEditRequestDto.getContent());

        CommentEditResponseDto commentEditResponseDto = modelMapper.map(comment, CommentEditResponseDto.class);
        commentEditResponseDto.setAuthor(comment.getMember().getName());
        commentEditResponseDto.setBoardId(boardId);
        commentEditResponseDto.setMemberId(member.getId());
        commentEditResponseDto.setSelected(comment.isSelected());

        return commentEditResponseDto;
    }
    
    //오버로딩
    //게시글이 존재하는지 검사 X
    @Transactional
    public CommentEditResponseDto editComment(Long commentId, CommentEditRequestDto commentEditRequestDto, String token) {

        Member member = jwtTokenProvider.getMember(token);

        Comment comment = retrieveOne(commentId);

        if (comment.getMember().getId() != member.getId()) {
            throw new NotOwnBoardException("게시글의 권한이 없습니다.");
        }

        if (comment.isSelected()) {
            throw new InValidUpdateException(CommentExceptionMessage.INVALID_COMMENT_UPDATE);
        }
        comment.changeContent(commentEditRequestDto.getContent());

        CommentEditResponseDto commentEditResponseDto = modelMapper.map(comment, CommentEditResponseDto.class);
        commentEditResponseDto.setAuthor(comment.getMember().getName());
        commentEditResponseDto.setBoardId(comment.getBoard().getId());
        commentEditResponseDto.setMemberId(member.getId());
        commentEditResponseDto.setSelected(comment.isSelected());

        return commentEditResponseDto;
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Long boardId, Long commentId, String token) {

        //게시글이 존재하는지 검사
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> {
                    throw new BoardNotFoundException(BoardExceptionMessage.BOARD_NOT_FOUND);
                }
        );

        //댓글이 존재하는 확인
        Comment comment = retrieveOne(commentId);

        Member member = jwtTokenProvider.getMember(token);

        if (comment.getMember().getId() != member.getId()) {
            throw new NotOwnBoardException("게시글의 권한이 없습니다.");
        }

        if (comment.isSelected()) {
            throw new InValidUpdateException(CommentExceptionMessage.INVALID_COMMENT_UPDATE);
        }

        commentRepository.deleteById(commentId);

        board.decreaseComments();
    }

    @Transactional
    public void deleteComment(Long commentId, String token) {

        //댓글이 존재하는 확인
        Comment comment = retrieveOne(commentId);
        Board board = comment.getBoard();

        Member member = jwtTokenProvider.getMember(token);

        if (comment.getMember().getId() != member.getId()) {
            throw new NotOwnBoardException("게시글의 권한이 없습니다.");
        }

        if (comment.isSelected()) {
            throw new InValidUpdateException(CommentExceptionMessage.INVALID_COMMENT_UPDATE);
        }

        commentRepository.deleteById(commentId);

        board.decreaseComments();
    }

    @Transactional
    public void updateCommentLike(Long boardId, Long commentId, String token) {
        Member member = jwtTokenProvider.getMember(token);

        //게시글이 존재하는지 검사
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> {
                    throw new BoardNotFoundException(BoardExceptionMessage.BOARD_NOT_FOUND);
                }
        );

        if (member.getLikeId().contains(commentId)) {
            throw new DuplicatedLikeException("이미 좋아요를 눌렀습니다.");
        }

        //회원의 좋아요 id 추가
        member.getLikeId().add(commentId);
        
        //댓글 엔티티 조회
        Comment comment = retrieveOne(commentId);
        int like = comment.getLikes();
        
        //댓글 좋아요 수 1 증가
        comment.setLikes(++like);
    }
    //개시글 좋아요 기능 오버로딩
    @Transactional
    public void updateCommentLike(Long commentId, String token) {
        Member member = jwtTokenProvider.getMember(token);

        if (member.getLikeId().contains(commentId)) {
            throw new DuplicatedLikeException("이미 좋아요를 눌렀습니다.");
        }

        //회원의 좋아요 id 추가
        member.getLikeId().add(commentId);

        //댓글 엔티티 조회
        Comment comment = retrieveOne(commentId);
        int like = comment.getLikes();

        //댓글 좋아요 수 1 증가
        comment.setLikes(++like);
    }

    @Transactional
    public void deleteAllOwnComment(Long memberId) {
        commentRepository.deleteAllByMemberId(memberId);
    }

    @Transactional
    public void selectComment(Long boardId, Long commentId, String token) {
        //request 객체의 헤더 부분에서 회원 조회
        Member member = jwtTokenProvider.getMember(token);

        //해당 게시글에도 채택 됨을 알기 위해 조회
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> {
                    throw new BoardNotFoundException(BoardExceptionMessage.BOARD_NOT_FOUND);
                }
        );

        //자신의 게시글이 아닐경우 채택할 수 없다.(인가)
        if (board.getMember().getId() != member.getId()) {
            throw new NotOwnBoardException("자신의 게시글만 채택할 수 있습니다.");
        }

        List<Comment> comments = commentRepository.findAllByBoardId(board.getId());

        for (Comment c : comments) {
            //이미 채택하였으면 에러 던짐
            if (c.isSelected()) {
                throw new InvalidSelectionException(CommentExceptionMessage.INVALID_SELECTION);
            }
        }
        
        //댓글에도 채택되었음으로 갱신
        Comment comment = retrieveOne(commentId);
        comment.chooseSelection(true);

        //채택한 사람도 증가
        member.increaseActiveScore(10);

        //채택당한 사람의 활동 점수 증가
        Member selectedMember = comment.getMember();
        selectedMember.increaseActiveScore(20);

        //게시글도 체크
        board.chooseSelection(true);
    }

    @Transactional
    public void selectComment(Long commentId, String token) {
        //request 객체의 헤더 부분에서 회원 조회
        Member member = jwtTokenProvider.getMember(token);
        
        //댓글 조회
        Comment comment = retrieveOne(commentId);
        //댓글의 게시글
        Board board = comment.getBoard();
        
        //자신의 게시글이 아닐경우 채택할 수 없다.(인가)
        if (board.getMember().getId() != member.getId()) {
            throw new NotOwnBoardException("자신의 게시글만 채택할 수 있습니다.");
        }

        List<Comment> comments = commentRepository.findAllByBoardId(board.getId());

        for (Comment c : comments) {
            //이미 채택하였으면 에러 던짐
            if (c.isSelected()) {
                throw new InvalidSelectionException(CommentExceptionMessage.INVALID_SELECTION);
            }
        }

        //댓글에도 채택되었음으로 갱신
        comment.chooseSelection(true);

        //채택한 사람도 증가
        member.increaseActiveScore(10);

        //채택당한 사람의 활동 점수 증가
        Member selectedMember = comment.getMember();
        selectedMember.increaseActiveScore(20);

        //게시글도 체크
        board.chooseSelection(true);
    }

}
