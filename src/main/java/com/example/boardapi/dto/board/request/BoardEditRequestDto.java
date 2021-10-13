package com.example.boardapi.dto.board.request;

import lombok.*;

/**
 * 게시글 변경 요청 DTO
 * 제목과 내용만 바꿀 수 있음
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class BoardEditRequestDto {

    private String title;

    private String content;
}
