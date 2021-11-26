package com.example.boardapi.repository.member;

import com.example.boardapi.entity.Member;

import java.util.Optional;

public interface MemberCustomRepository {

    Optional<Member> findByLoginId(String loginId);

}
