package com.example.boardapi.dto.board.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardPageResponseDto {

    int currentPage;

    int totalPage;

    int totalElements;

    List<BoardRetrieveResponseDto> contents;
}
