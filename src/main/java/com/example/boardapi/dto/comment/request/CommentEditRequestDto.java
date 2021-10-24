package com.example.boardapi.dto.comment.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@ApiModel(description = "댓글 수정 요청 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CommentEditRequestDto {

    @ApiModelProperty(required = true, value = "수정할 댓글 내용", example = "처음 가입했어요!")
    @NotBlank
    private String content;
}
