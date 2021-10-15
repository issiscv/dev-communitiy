package com.example.boardapi.dto.board.request;

import io.swagger.annotations.ApiModel;
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

    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
