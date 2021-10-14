package com.example.boardapi.service;

import com.example.boardapi.domain.Comment;
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
        return commentRepository.findById(commentId).orElse(null);
    }

    /**
     * 전체 조회
     */
    public List<Comment> retrieveAll() {
        return commentRepository.findAll();
    }

    public List<Comment> retrieveOneByBoardId(Long boardId) {
        List<Comment> allByBoardId;

        try {
            allByBoardId = commentRepository.findAllByBoardId(boardId);
        } catch (Exception e) {
            throw new UserNotFoundException("해당 게시글이 존재하지 않습니다.");
        }

        return allByBoardId;
    }
}
