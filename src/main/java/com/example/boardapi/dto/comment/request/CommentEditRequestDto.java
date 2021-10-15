package com.example.boardapi.dto.comment.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CommentEditRequestDto {

    private String content;
}
