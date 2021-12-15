package com.example.boardapi.dto.notice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@ApiModel(description = "알림 페이징 조회 응답 DTO")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeRetrieveAllPagingResponseDto {

    @ApiModelProperty(required = true, value = "현재 페이지", example = "1")
    private int currentPage;

    @ApiModelProperty(required = true, value = "총 페이지", example = "10")
    private int totalPages;

    @ApiModelProperty(required = true, value = "총 알림 수", example = "25")
    private int totalElements;

    @ApiModelProperty(required = true, value = "알림 DTO")
    private List<NoticeRetrieveResponseDto> content;
}
