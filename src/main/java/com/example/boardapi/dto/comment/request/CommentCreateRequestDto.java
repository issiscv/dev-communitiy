package com.example.boardapi.dto.comment.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class CommentCreateRequestDto {

    @ApiModelProperty(required = true, value = "댓글 내용", example = "댓글 감사합니다.")
    @NotBlank
    private String content;
}
