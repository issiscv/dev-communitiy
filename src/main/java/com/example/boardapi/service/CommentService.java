package com.example.boardapi.service;

import com.example.boardapi.dto.comment.request.CommentEditRequestDto;
import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.Comment;
import com.example.boardapi.entity.Member;
import com.example.boardapi.exception.CommentNotFoundException;
import com.example.boardapi.exception.InValidUpdateException;
import com.example.boardapi.exception.InvalidSelectionException;
import com.example.boardapi.exception.message.CommentExceptionMessage;
import com.example.boardapi.repository.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
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
            throw new CommentNotFoundException(CommentExceptionMessage.COMMENT_NOT_FOUND);
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
        return commentRepository.findAllByBoardId(boardId);
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public Comment editComment(Long id, CommentEditRequestDto commentEditRequestDto) {
        Comment comment = retrieveOne(id);
        if (comment.isSelected()) {
            throw new InValidUpdateException(CommentExceptionMessage.INVALID_COMMENT_UPDATE);
        }
        comment.changeContent(commentEditRequestDto.getContent());
        return comment;
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Board board, Long id) {
        Comment comment = retrieveOne(id);

        if (comment.isSelected()) {
            throw new InValidUpdateException(CommentExceptionMessage.INVALID_COMMENT_UPDATE);
        }

        try {
            commentRepository.deleteById(id);

        } catch (IllegalArgumentException e) {
            throw new CommentNotFoundException(CommentExceptionMessage.COMMENT_NOT_FOUND);
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

    @Transactional
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
                throw new InvalidSelectionException(CommentExceptionMessage.INVALID_SELECTION);
            }
        }

        comment.chooseSelection(true);

        //채택한 사람도 증가
        Member boardMember = board.getMember();
        boardMember.increaseActiveScore(10);

        //채택당한 사람의 활동 점수 증가
        Member member = comment.getMember();
        member.increaseActiveScore(20);

        //게시글도 체크
        board.chooseSelection(true);
    }

}
