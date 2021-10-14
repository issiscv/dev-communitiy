package com.example.boardapi.dto.comment.response;

import com.example.boardapi.domain.Board;
import com.example.boardapi.domain.Member;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateResponseDto {

    private Long commentId;//댓글 기본키

    private Long boardId;//게시글 기본키

    private String author;//댓글 작성

    private String content;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    private String createdBy;

    private String lastModifiedBy;
}
