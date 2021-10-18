package com.example.boardapi.dto.comment.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentEditResponseDto {

    private Long id;//댓글 기본키

    private Long boardId;//게시글 기본키

    private String author;//댓글 작성

    private String content;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;
}
