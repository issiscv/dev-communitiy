package com.example.boardapi.repository;

import com.example.boardapi.entity.Comment;
import com.example.boardapi.entity.QBoard;
import com.example.boardapi.entity.QComment;
import com.example.boardapi.entity.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.example.boardapi.entity.QBoard.*;
import static com.example.boardapi.entity.QComment.*;
import static com.example.boardapi.entity.QMember.*;

@Slf4j
@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;
    
    //댓글 엔티티 조회, 회원 엔티티와 페치 조인
    @Override
    public Optional<Comment> findByIdFetchJoinWithMember(Long commentId) {

        Comment result = queryFactory
                .selectFrom(comment)
                .join(comment.member, member).fetchJoin()
                .where(comment.id.eq(commentId))
                .fetchOne();

        return Optional.of(result);
    }

    //게시글에 해당하는 댓글 조회
    @Override
    public List<Comment> findAllByBoardId(Long boardId) {
        List<Comment> result = queryFactory
                .selectFrom(comment)
                .join(comment.member, member).fetchJoin()
                .where(comment.board.id.eq(boardId))
                .fetch();
        return result;
    }

    //게시글 삭제 시 외래키를 참조하므로 사전에 삭제를 한다.
    @Override
    public void deleteAllByBoardId(Long boardId) {
        queryFactory
                .delete(comment)
                .where(comment.board.id.eq(boardId))
                .execute();

        em.flush();
        em.clear();
    }

    //특정 회원이 작성한 게시글 조회
    @Override
    public List<Comment> findAllByMemberId(Long memberId) {
        List<Comment> result = queryFactory
                .selectFrom(comment)
                .join(comment.board, board).fetchJoin()
                .join(comment.member, member).fetchJoin()
                .where(comment.member.id.eq(memberId))
                .fetch();

        return result;
    }
    
    //특정 회우언이 작성한 게시글 삭제
    @Override
    public void deleteAllByMemberId(Long memberId) {

        queryFactory
                .delete(comment)
                .where(comment.member.id.eq(memberId))
                .execute();

        em.flush();
        em.clear();
    }
}
