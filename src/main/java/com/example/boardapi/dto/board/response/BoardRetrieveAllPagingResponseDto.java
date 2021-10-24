package com.example.boardapi.dto.board.response;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardRetrieveAllPagingResponseDto {
    
    //현재 페이지
    private int currentPage;
    //총 페이지 수
    private int totalPage;
    //총 게시글 수
    private int totalElements;

    List<BoardRetrieveOneResponseDto> contents;
}
