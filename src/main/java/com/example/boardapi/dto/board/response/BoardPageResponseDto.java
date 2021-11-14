package com.example.boardapi.dto.board.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//회원의 게시글 조회시 사용
public class BoardPageResponseDto {

    int currentPage;

    int totalPage;

    int totalElements;

    List<BoardRetrieveResponseDto> contents;
}
