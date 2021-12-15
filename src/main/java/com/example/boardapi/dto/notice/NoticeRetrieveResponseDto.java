package com.example.boardapi.dto.notice;

import com.example.boardapi.entity.enumtype.MessageType;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeRetrieveResponseDto {

    private Long noticeId;

    private Long memberId;

    private Long boardId;

    private String title;

    //누가
    private String loginId;

    //메세지 유형(댓글 달림, 좋아요 눌림, 채택 당함)
    private MessageType messageType;

    //읽었는지
    private boolean isChecked;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;
}
