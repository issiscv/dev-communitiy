package com.example.boardapi.dto.board.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@ApiModel(description = "1 주일 내의 좋아요 순으로 정렬한 게시글 조회 응답 DTO")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardRetrieveAllByWeekResponseDto {

    @ApiModelProperty(required = true, value = "해당 페이지의 게시글 들")
    List<BoardRetrieveResponseDto> contents;
}
