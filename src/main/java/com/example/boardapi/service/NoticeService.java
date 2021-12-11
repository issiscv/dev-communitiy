package com.example.boardapi.service;

import com.example.boardapi.entity.Board;
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

    //댓글 작성 시 쓰이는 메서드
    @Transactional
    public void saveNotice(Long boardId, String token) {

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

    /**
     * 좋아요
     * 채택 알림 메서드 개발
     */

}
