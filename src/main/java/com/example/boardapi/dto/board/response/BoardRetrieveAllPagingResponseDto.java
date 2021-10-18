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

    List<BoardRetrieveOneResponseDto> contents;
}
