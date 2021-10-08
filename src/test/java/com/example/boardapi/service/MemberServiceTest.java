package com.example.boardapi.service;

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
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Test
    void 회원가입() {
        Member member = Member.builder()
                .loginId("abcd")
                .password("1234")
                .name("김상운")
                .age(24)
                //.address(new Address("서울", "강북구", "1-113"))
                .build();

        Member saveMember = memberService.join(member);

        Assertions.assertThat(saveMember).isEqualTo(memberService.retrieveOne(saveMember.getId()));
    }
    
}