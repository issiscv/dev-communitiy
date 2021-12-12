package com.example.boardapi.dto.notice;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeRetrieveAllPagingResponseDto {

    private int currentPage;

    private int totalPages;

    private int totalElements;

    private List<NoticeRetrieveResponseDto> noticeRetrieveResponseDtoList;
}
