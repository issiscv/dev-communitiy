package com.example.boardapi.repository.scrap;

import com.example.boardapi.entity.Scrap;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.boardapi.entity.QBoard.board;
import static com.example.boardapi.entity.QScrap.scrap;

@RequiredArgsConstructor
public class ScrapCustomRepositoryImpl implements ScrapCustomRepository{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public List<Scrap> findByMemberId(Long memberId) {
        List<Scrap> result = queryFactory
                .selectFrom(scrap)
                .join(scrap.board, board).fetchJoin()
                .where(scrap.member.id.eq(memberId))
                .fetch();
        return result;
    }

    @Override
    public void deleteByBoardId(Long boardId) {
        queryFactory
                .delete(scrap)
                .where(scrap.board.id.eq(boardId))
                .execute();

        em.flush();
        em.clear();
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        queryFactory
                .delete(scrap)
                .where(scrap.member.id.eq(memberId))
                .execute();

        em.flush();
        em.clear();
    }
}
