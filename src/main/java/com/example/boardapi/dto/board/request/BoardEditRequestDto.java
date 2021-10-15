package com.example.boardapi.dto.board.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * 게시글 변경 요청 DTO
 * 제목과 내용만 바꿀 수 있음
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
@ApiModel(description = "게시글 수정 DTO")
public class BoardEditRequestDto {

    @ApiModelProperty(required = true, value = "수정할 게시글 제목", example = "안녕하세요! 반가워요!")
    @NotBlank
    private String title;

    @ApiModelProperty(required = true, value = "수정할 게시글 내용", example = "처음 가입했어요!")
    @NotBlank
    private String content;
}
