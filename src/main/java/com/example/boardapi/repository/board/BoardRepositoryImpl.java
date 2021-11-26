package com.example.boardapi.repository.board;

import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.enumtype.BoardType;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.boardapi.entity.QBoard.board;

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

    @Override
    public Page<Board> findAllWithPaging(Pageable pageable, String type, String sort) {
        QueryResults<Board> results = queryFactory
                .selectFrom(board)
                .where(boardTypeEq(type))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderBySortCond(sort), board.createdDate.desc())
                .fetchResults();

        List<Board> result = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(result, pageable, total);
    }

    private OrderSpecifier orderBySortCond(String sort) {
        if (sort.equals("likes")) {
            return board.likes.desc();
        } else if (sort.equals("commentSize")) {
            return board.commentSize.desc();
        } else if (sort.equals("views")) {
            return board.views.desc();
        } else {
            //createdDate
            return board.createdDate.desc();
        }
    }

    //keyword 로 검색
    @Override
    public Page<Board> findAllByKeyWordWithPaging(Pageable pageable, String searchCond, String keyWord, String type) {
        List<Board> content = queryFactory
                .selectFrom(board)
                .where(searchEq(searchCond, keyWord), boardTypeEq(type))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(board.createdDate.desc())
                .fetch();

        long total = queryFactory
                .selectFrom(board)
                .where(searchEq(searchCond, keyWord), boardTypeEq(type))
                .fetchCount();


        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression searchEq(String searchCond, String keyWord) {
        if (searchCond.equals("title")) {
            return board.title.contains(keyWord);
        } else if (searchCond.equals("content")) {
            return board.content.contains(keyWord);
        } else {
            //all
            return board.title.contains(keyWord).or(board.content.contains(keyWord));
        }
    }

    private BooleanExpression boardTypeEq(String type) {
        if (type.equals("free")) {
            return board.boardType.eq(BoardType.FREE);
        } else if (type.equals("qna")) {
            return board.boardType.eq(BoardType.QNA);
        } else {
            //tech
            return board.boardType.eq(BoardType.TECH);
        }
    }

    //주간 베스트, 좋아요 순으로 정렬
    @Override
    public Page<Board> findBestBoardsBySevenDaysWithPaging(Pageable pageable, LocalDateTime beforeSevenDay) {
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

    @Override
    public void deleteByBoardId(Long boardId) {
        queryFactory
                .delete(board)
                .where(board.id.eq(boardId))
                .execute();

        em.flush();
        em.clear();;
    }
}
