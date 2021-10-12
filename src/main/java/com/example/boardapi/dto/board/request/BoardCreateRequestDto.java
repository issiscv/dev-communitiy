package com.example.boardapi.dto.board.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class BoardCreateRequestDto {
    //제목
    @NotBlank
    private String title;
    
    //내용
    @NotBlank
    private String content;
}
