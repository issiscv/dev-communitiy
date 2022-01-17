package com.example.boardapi.service;

import com.example.boardapi.dto.notice.NoticeRetrieveAllPagingResponseDto;
import com.example.boardapi.dto.notice.NoticeRetrieveResponseDto;
import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.Comment;
import com.example.boardapi.entity.Member;
import com.example.boardapi.entity.Notice;
import com.example.boardapi.entity.enumtype.MessageType;
import com.example.boardapi.exception.NotOwnNoticeException;
import com.example.boardapi.exception.NoticeNotFoundException;
import com.example.boardapi.exception.message.NoticeExceptionMessage;
import com.example.boardapi.config.jwt.JwtTokenProvider;
import com.example.boardapi.repository.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final BoardService boardService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CommentService commentService;
    private final ModelMapper modelMapper;

    //자신의 게시글에 다른이가 댓글 작성 시 알림
    @Transactional
    public void saveNoticeOnComment(Long boardId, String token) {

        //댓글을 쓰는 회원
        Member commentMember = jwtTokenProvider.getMember(token);
        
        //게시글의 회원
        Board board = boardService.retrieveOne(boardId);
        Member member = board.getMember();

        //게시글 회원과 댓긓 회원이 같은 경우 return
        if (commentMember == member) {
            return;
        }

        Notice notice = Notice.builder()
                .member(member)
                .board(board)
                .title(board.getTitle())
                .loginId(commentMember.getLoginId())//액션을 취한 사용자의 아이디
                .messageType(MessageType.COMMENT)//어떤 유형인지
                .isChecked(false).
                        build();

        noticeRepository.save(notice);
    }

    //자신의 게시글에 다른이가 좋아요를 누룰 시 알림
    @Transactional
    public void saveNoticeOnBoardLike(Long boardId, String token) {
        //좋아요를 누르는 회원
        Member likeMember = jwtTokenProvider.getMember(token);

        //게시글의 회원
        Board board = boardService.retrieveOne(boardId);
        Member member = board.getMember();

        //게시글 회원과 댓긓 회원이 같은 경우 return
        if (likeMember == member) {
            return;
        }

        Notice notice = Notice.builder()
                .member(member)
                .board(board)
                .title(board.getTitle())
                .loginId(likeMember.getLoginId())//액션을 취한 사용자의 아이디
                .messageType(MessageType.BOARD_LIKE)//어떤 유형인지
                .isChecked(false).
                        build();

        noticeRepository.save(notice);
    }

    //자신이 쓴 댓글의 다른이가 좋아요를 누를 시 알림
    @Transactional
    public void saveNoticeOnCommentLike(Long commentId, String token) {
        //댓글 좋아요를 누르는 회원
        Member likeMember = jwtTokenProvider.getMember(token);

        //댓글의 회원
        Comment comment = commentService.retrieveOne(commentId);
        Member member = comment.getMember();

        //게시글 회원과 댓긓 회원이 같은 경우 return
        if (likeMember == member) {
            return;
        }

        Notice notice = Notice.builder()
                .member(member)
                .board(comment.getBoard())
                .title(comment.getBoard().getTitle())
                .loginId(likeMember.getLoginId())//액션을 취한 사용자의 아이디
                .messageType(MessageType.COMMENT_LIKE)//어떤 유형인지
                .isChecked(false).
                        build();

        noticeRepository.save(notice);
    }

    //자신이 쓴 댓글이 채택되었을 때 알림
    @Transactional
    public void saveNoticeOnSelection(Long commentId, String token) {

        //댓글 채택을 누르는 회원
        Member selectionMember = jwtTokenProvider.getMember(token);

        //댓글의 회원
        Comment comment = commentService.retrieveOne(commentId);
        Member member = comment.getMember();

        //게시글 회원과 댓글 회원이 같은 경우 return
        if (selectionMember == member) {
            return;
        }

        Notice notice = Notice.builder()
                .member(member)
                .board(comment.getBoard())
                .title(comment.getBoard().getTitle())
                .loginId(selectionMember.getLoginId())//액션을 취한 사용자의 아이디
                .messageType(MessageType.SELECTION)//어떤 유형인지
                .isChecked(false).
                        build();

        noticeRepository.save(notice);
    }

    //회원의 알림 조회
    public NoticeRetrieveAllPagingResponseDto retrieveNoticeDtoList(int page, Long memberId) {

        PageRequest pageRequest = PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Notice> retrieveAllNoticeWithPaging = noticeRepository.findByMemberIdWithPaging(pageRequest, memberId);

        List<Notice> content = retrieveAllNoticeWithPaging.getContent();
        int totalPages = retrieveAllNoticeWithPaging.getTotalPages();
        int totalElements = (int) retrieveAllNoticeWithPaging.getTotalElements();

        List<NoticeRetrieveResponseDto> noticeRetrieveResponseDtos = content.stream().map(n -> {
            NoticeRetrieveResponseDto noticeRetrieveResponseDto = modelMapper.map(n, NoticeRetrieveResponseDto.class);
            noticeRetrieveResponseDto.setNoticeId(n.getId());
            noticeRetrieveResponseDto.setMemberId(n.getMember().getId());
            noticeRetrieveResponseDto.setBoardId(n.getBoard().getId());
            noticeRetrieveResponseDto.setBoardType(n.getBoard().getBoardType());

            return noticeRetrieveResponseDto;
        }).collect(Collectors.toList());

        NoticeRetrieveAllPagingResponseDto noticeRetrieveAllPagingResponseDto =
                new NoticeRetrieveAllPagingResponseDto(page, totalPages, totalElements, noticeRetrieveResponseDtos);

        return noticeRetrieveAllPagingResponseDto;
    }

    @Transactional
    public void updateNoticeOne(Long memberId, Long noticeId, String token) {

        //해당 회원이 존재하는지 검사 및 토큰 해석
        Member member = jwtTokenProvider.getMember(token);

        //알림 조회
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(
                () -> {
                    throw new NoticeNotFoundException(NoticeExceptionMessage.NOTICE_NOT_FOUND);
                }
        );

        //토큰의 회원이 알림 상태 변경의 권한이 있는지
        if (!member.getId().equals(memberId) || member.getId() != notice.getMember().getId()) {
            throw new NotOwnNoticeException(NoticeExceptionMessage.NOT_OWN_NOTICE);
        }

        //알림 상태 변경
        notice.changeChecked();
    }

    @Transactional
    public void updateNoticeAll(Long memberId, String token) {
        
        //해당 회원이 존재하는지 검사 및 토큰 해석
        Member member = jwtTokenProvider.getMember(token);

        //토큰의 회원이 알림 상태 변경의 권한이 있는지
        if (!member.getId().equals(memberId)) {
            throw new NotOwnNoticeException(NoticeExceptionMessage.NOT_OWN_NOTICE);
        }
        
        //벌크 연산
        noticeRepository.updateNoticeChecked(memberId);
    }

    public Long countNotCheckedNotice(Long memberId) {
        return noticeRepository.countByNotCheckedAndMemberId(memberId);
    }
}
