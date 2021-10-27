package com.example.boardapi.service;

import com.example.boardapi.domain.Board;
import com.example.boardapi.domain.Comment;
import com.example.boardapi.dto.comment.request.CommentEditRequestDto;
import com.example.boardapi.exception.exception.BoardNotFoundException;
import com.example.boardapi.exception.exception.CommentNotFoundException;
import com.example.boardapi.exception.exception.UserNotFoundException;
import com.example.boardapi.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;

    /**
     * 댓글 저장
     */
    @Transactional
    public Comment save(Comment comment) {
        Comment saveComment = commentRepository.save(comment);
        return saveComment;
    }

    /**
     * 단건 조회 댓글까지
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
    public List<Comment> retrieveOneByBoardId(Long boardId) {
        List<Comment> allByBoardId;

        try {
            allByBoardId = commentRepository.findAllByBoardId(boardId);
        } catch (Exception e) {
            throw new BoardNotFoundException("해당 게시글이 존재하지 않습니다.");
        }

        return allByBoardId;
    }

    @Transactional
    public Comment editComment(Long id, CommentEditRequestDto commentEditRequestDto) {
        Comment comment = retrieveOne(id);
        comment.setContent(commentEditRequestDto.getContent());
        return comment;
    }

    @Transactional
    public void deleteComment(Long id) {
        try {
            commentRepository.deleteById(id);
        } catch (Exception e) {
            throw new CommentNotFoundException("해당 댓글을 찾을 수 없습니다.");
        }
    }

    public List<Comment> retrieveAllOwnComment(Long id) {
        return commentRepository.findAllByMemberId(id);
    }

    @Transactional
    public void updateCommentLike(Long commentId) {
        Comment comment = retrieveOne(commentId);
        int like = comment.getLikes();
        comment.setLikes(++like);
    }
}
