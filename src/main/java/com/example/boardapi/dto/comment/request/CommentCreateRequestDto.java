package com.example.boardapi.dto.comment.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class CommentCreateRequestDto {

    private String content;
}
