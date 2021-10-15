package com.example.boardapi.dto.board.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
@ApiModel(description = "게시글 생성 DTO")
public class BoardCreateRequestDto {
    //제목
    @ApiModelProperty(required = true, value = "게시글 제목", example = "안녕하세요! 반가워요!")
    @NotBlank
    private String title;
    
    //내용
    @ApiModelProperty(required = true, value = "게시글 내용", example = "처음 가입했어요!")
    @NotBlank
    private String content;
}
