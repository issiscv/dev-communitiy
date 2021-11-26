package com.example.boardapi.repository.member;

import com.example.boardapi.entity.Member;
import com.example.boardapi.entity.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class MemberCustomRepositoryImpl implements MemberCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Member> findByLoginId(String loginId) {

        Member member = queryFactory
                .selectFrom(QMember.member)
                .where(QMember.member.loginId.eq(loginId))
                .fetchOne();

        return Optional.ofNullable(member);
    }
}
