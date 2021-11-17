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

        em.persist(member);


        Board board = new Board(member, "제목", "본문");
        Board board1 = new Board(member, "제목", "본문");
        Board board2 = new Board(member, "제목", "본문");
        Board board3 = new Board(member, "제목", "본문");
        Board board4 = new Board(member, "제목", "본문");
        Board board5 = new Board(member, "제목", "본문");

        em.persist(board);
        em.persist(board1);
        em.persist(board2);
        em.persist(board3);
        em.persist(board4);
        em.persist(board5);

        em.flush();
        em.clear();

        Member member1 = memberRepository.findById(member.getId()).orElse(null);

        int size = member1.getScrapList().size();
        System.out.println("size = " + size);
    }
}
