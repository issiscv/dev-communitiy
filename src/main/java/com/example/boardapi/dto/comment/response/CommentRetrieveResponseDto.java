package com.example.boardapi.dto.comment.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentRetrieveResponseDto {

    private String author;//댓글 작성자

    private String content;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

}
