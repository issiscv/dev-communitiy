package com.example.boardapi.repository;

import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.enumtype.BoardType;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.boardapi.entity.QBoard.*;

@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardCustomRepository{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public List<Board> findBoardByMember(Long memberId) {
        return queryFactory
                .selectFrom(board)
                .where(board.member.id.eq(memberId))
                .fetch();
    }

    //keyword 로 검색
    @Override
    public Page<Board> findAllByKeyWordWithPaging(Pageable pageable, String keyWord) {
        List<Board> content = queryFactory
                .selectFrom(board)
                .where(keyWordLike(keyWord))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(board.createdDate.desc())
                .fetch();

        long total = queryFactory
                .selectFrom(board)
                .where(keyWordLike(keyWord))
                .fetchCount();


        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression keyWordLike(String keyWord) {
        return StringUtils.hasText(keyWord) ? board.content.contains(keyWord).or(board.title.contains(keyWord)) : null;
    }

    //주간 베스트, 좋아요 순으로 정렬
    @Override
    public Page<Board> findByBoardTypeInDateBestBoardsWithPaging(Pageable pageable, LocalDateTime beforeSevenDay) {
        List<Board> content = queryFactory
                .selectFrom(board)
                .where(board.createdDate.goe(beforeSevenDay))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(board.likes.desc(), board.createdDate.desc())
                .fetch();//count 쿼리 같이 안나감

        long total = queryFactory
                .selectFrom(board)
                .where(board.createdDate.goe(beforeSevenDay))
                .fetchCount();//여기서 계산


        return new PageImpl<>(content, pageable, total);
    }
    
    //회원이 작성한 게시글
    @Override
    public Page<Board> findBoardByMemberWithPaging(Pageable pageable, Long memberId) {
        List<Board> content = queryFactory
                .selectFrom(board)
                .where(board.member.id.eq(memberId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(board.createdDate.desc())
                .fetch();

        long total = queryFactory
                .selectFrom(board)
                .where(board.member.id.eq(memberId))
                .fetchCount();


        return new PageImpl<>(content, pageable, total);
    }

    //벌크 연산 후 영속성 컨텍스트 초기화
    @Override
    public void deleteAllByMemberId(Long memberId) {
        queryFactory
                .delete(board)
                .where(board.member.id.eq(memberId))
                .execute();

        em.flush();
        em.clear();
    }
}
