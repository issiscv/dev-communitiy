package com.example.boardapi.dto.comment.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class CommentCreateRequestDto {

    @NotBlank
    private String content;
}
