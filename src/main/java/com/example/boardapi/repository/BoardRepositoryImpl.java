package com.example.boardapi.repository;

import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.enumtype.BoardType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
//
//    //전체 조회, 날짜 내림차순 정렬
//    @Override
//    public Page<Board> findAllWithPaging(Pageable pageable, BoardType boardType) {
//        List<Board> content = queryFactory
//                .selectFrom(board)
//                .where(board.boardType.eq(boardType))
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .orderBy(board.createdDate.desc())
//                .fetch();
//
//        long total = queryFactory
//                .selectFrom(board)
//                .where(board.boardType.eq(boardType))
//                .fetchCount();
//
//
//        return new PageImpl<>(content, pageable, total);
//    }
    
    //주간 베스트, 좋아요 순으로 정렬
    @Override
    public Page<Board> findByBoardTypeInDateBestBoardsWithPaging(Pageable pageable, LocalDateTime beforeSevenDay) {
        pageable.getSort();
        List<Board> content = queryFactory
                .selectFrom(board)
                .where(board.createdDate.goe(beforeSevenDay))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(board.likes.desc())
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
                .where(board.member.id.eq(memberId));

        em.flush();
        em.clear();
    }
}
