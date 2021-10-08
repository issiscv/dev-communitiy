package com.example.boardapi.service;

import com.example.boardapi.domain.Board;
import com.example.boardapi.domain.Comment;
import com.example.boardapi.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class CommentServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private CommentService commentService;

    @Test
    void 댓글_작성() {


        Member member = Member.builder()
                .loginId("abcd")
                .password("1234")
                .name("김상운")
                .age(24)
                //.address(new Address("서울", "강북구", "1-113"))
                .build();

        Board board = Board.builder()
                .title("게시판 이용 규칙")
                .text("1. 모두랑 사이좋게 지내기")
                .member(member)
                .build();

        Comment comment = Comment.builder()
                .text("좋아요 사이좋게 지내요!")
                .member(member)
                .board(board)
                .build();

        Member saveMember = memberService.join(member);
        Board saveBoard = boardService.save(board);
        Comment saveComment = commentService.save(comment);

        Assertions.assertThat(saveComment).isEqualTo(commentService.retrieveOne(saveComment.getId()));
    }
}