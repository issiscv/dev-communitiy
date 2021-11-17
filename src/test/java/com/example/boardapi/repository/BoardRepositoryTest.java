package com.example.boardapi.repository;

import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager em;

    @Test
    public void noFetchJoin() {
        Member member = new Member("김상운", 24, "ROLE_USER");
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(
                        "김상운", "1234", new ArrayList<SimpleGrantedAuthority>()));
        memberRepository.save(member);

        Board board = new Board(member, "제목", "본문");

        boardRepository.save(board);

        em.flush();
        em.clear();

        Board boardTest = boardRepository.findById(board.getId()).orElse(null);

        System.out.println("//1//");
        System.out.println("boardTest.getMember().getName() = " + boardTest.getMember());
        System.out.println("//1//");

    }
}
