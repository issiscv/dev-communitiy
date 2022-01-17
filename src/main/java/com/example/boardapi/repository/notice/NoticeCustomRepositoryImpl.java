package com.example.boardapi.repository.notice;


import com.example.boardapi.entity.Notice;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.example.boardapi.entity.QNotice.notice;


@RequiredArgsConstructor
public class NoticeCustomRepositoryImpl implements NoticeCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Notice> findByMemberId(Long memberId) {

        List<Notice> results = queryFactory
                .selectFrom(notice)
                .where(notice.member.id.eq(memberId))
                .fetch();

        return results;
    }

    @Override
    public Page<Notice> findByMemberIdWithPaging(Pageable pageable, Long memberId) {

        QueryResults<Notice> results = queryFactory
                .selectFrom(notice)
                .where(notice.member.id.eq(memberId))
                .orderBy(notice.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Notice> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public void updateNoticeChecked(Long memberId) {
        queryFactory
                .update(notice)
                .set(notice.isChecked, true)
                .where(notice.member.id.eq(memberId))
                .execute();
    }

    @Override
    public Long countByNotCheckedAndMemberId(Long memberId) {
        return queryFactory
                .select(notice.count())
                .from(notice)
                .where(notice.member.id.eq(memberId), notice.isChecked.eq(false))
                .fetchCount();
    }
}
