package com.example.boardapi.service;

import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.Comment;
import com.example.boardapi.entity.Member;
import com.example.boardapi.entity.Notice;
import com.example.boardapi.entity.enumtype.MessageType;
import com.example.boardapi.jwt.JwtTokenProvider;
import com.example.boardapi.repository.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final BoardService boardService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CommentService commentService;

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
                .loginId(selectionMember.getLoginId())//액션을 취한 사용자의 아이디
                .messageType(MessageType.SELECTION)//어떤 유형인지
                .isChecked(false).
                        build();

        noticeRepository.save(notice);
    }
    
    

}
