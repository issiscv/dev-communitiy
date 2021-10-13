package com.example.boardapi.dto.board.request;

import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
@ApiModel(description = "게시글 생성 DTO")
public class BoardCreateRequestDto {
    //제목
    @NotBlank
    private String title;
    
    //내용
    @NotBlank
    private String content;
}
