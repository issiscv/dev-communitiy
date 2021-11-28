package com.example.boardapi.dto.board.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@ApiModel(description = "게시글 조회 응답 DTO")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardRetrieveAllPagingResponseDto {
    
    @ApiModelProperty(required = true, value = "현재 페이지", example = "2")
    private int currentPage;

    @ApiModelProperty(required = true, value = "총 페이지 수", example = "10")
    private int totalPages;

    @ApiModelProperty(required = true, value = "총 게시글 수", example = "252")
    private int totalElements;

    @ApiModelProperty(required = true, value = "해당 페이지의 게시글 들")
    List<BoardRetrieveResponseDto> contents;
}
