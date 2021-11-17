package com.example.boardapi.service;

import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.Comment;
import com.example.boardapi.entity.Member;
import com.example.boardapi.dto.comment.request.CommentEditRequestDto;
import com.example.boardapi.exception.exception.BoardNotFoundException;
import com.example.boardapi.exception.exception.CommentNotFoundException;
import com.example.boardapi.exception.exception.NotValidUpdateException;
import com.example.boardapi.exception.exception.SelectedCommentExistException;
import com.example.boardapi.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final EntityManager em;
    /**
     * 댓글 저장
     */
    @Transactional
    public Comment save(Member member, Board board, Comment comment) {
        Comment saveComment = commentRepository.save(comment);

        member.increaseActiveScore(2);
        board.increaseComments();

        return saveComment;
    }

    /**
     * 단건 조회
     */
    public Comment retrieveOne(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> {
            throw new CommentNotFoundException("해당 댓글이 없습니다.");
        }
        );
    }

    /**
     * 전체 조회
     */
    public List<Comment> retrieveAll() {
        return commentRepository.findAll();
    }

    /**
     * 특정 게시글의 댓글
     */
    public List<Comment> retrieveAllByBoardId(Long boardId) {
        List<Comment> allByBoardId;

        try {
            allByBoardId = commentRepository.findAllByBoardId(boardId);
        } catch (Exception e) {
            throw new BoardNotFoundException("해당 게시글이 존재하지 않습니다.");
        }

        return allByBoardId;
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public Comment editComment(Long id, CommentEditRequestDto commentEditRequestDto) {
        Comment comment = retrieveOne(id);
        if (comment.isSelected()) {
            throw new NotValidUpdateException("채택된 댓글은 수정할 수 없습니다.");
        }
        comment.setContent(commentEditRequestDto.getContent());
        return comment;
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Board board, Long id) {
        Comment comment = retrieveOne(id);

        if (comment.isSelected()) {
            throw new NotValidUpdateException("채택된 댓글은 삭제할 수 없습니다.");
        }

        try {
            commentRepository.deleteById(id);

        } catch (IllegalArgumentException e) {
            throw new CommentNotFoundException("해당 댓글을 찾을 수 없습니다.");
        }

        board.decreaseComments();
    }

    public List<Comment> retrieveAllOwnComment(Long id) {
        return commentRepository.findAllByMemberId(id);
    }

    @Transactional
    public void updateCommentLike(Member member, Long commentId) {
        member.getLikeId().add(commentId);
        Comment comment = retrieveOne(commentId);
        int like = comment.getLikes();
        comment.setLikes(++like);
    }

    public void deleteAllOwnComment(Long memberId) {
        commentRepository.deleteAllByMemberId(memberId);
    }

    @Transactional
    public void selectComment(Board board, Long commentId) {
        
        //페치조인
        List<Comment> comments = retrieveAllByBoardId(board.getId());
        Comment comment = retrieveOne(commentId);

        for (Comment c : comments) {
            //이미 채택하였으면 에러 던짐
            if (c.isSelected()) {
                throw new SelectedCommentExistException("이미 댓글을 채택하셧습니다.");
            }
        }

        comment.setSelected(true);

        //채택한 사람도 증가
        Member boardMember = board.getMember();
        boardMember.increaseActiveScore(10);

        //채택당한 사람의 활동 점수 증가
        Member member = comment.getMember();
        member.increaseActiveScore(20);

        //게시글도 체크
        board.setSelected(true);
    }
}
