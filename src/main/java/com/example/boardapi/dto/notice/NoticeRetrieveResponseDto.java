package com.example.boardapi.dto.notice;

import com.example.boardapi.entity.enumtype.BoardType;
import com.example.boardapi.entity.enumtype.MessageType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

@ApiModel(description = "알림 단건 조회 응답 DTO")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeRetrieveResponseDto {


    @ApiModelProperty(required = true, value = "알림 PK", example = "26")
    private Long noticeId;

    @ApiModelProperty(required = true, value = "회원 PK", example = "3")
    private Long memberId;

    @ApiModelProperty(required = true, value = "게시글 PK", example = "20")
    private Long boardId;

    @ApiModelProperty(required = true, value = "게시글 종륲", example = "QNA, TECH, FREE")
    private BoardType boardType;

    @ApiModelProperty(required = true, value = "게시글 제목", example = "제목입니다.")
    private String title;

    //누가
    @ApiModelProperty(required = true, value = "누가", example = "qwer123")
    private String loginId;

    //메세지 유형(댓글 달림, 좋아요 눌림, 채택 당함)
    @ApiModelProperty(required = true, value = "알림 유형", example = "COMMENT, BOARD_LIKE, COMMENT_LIKE, SELECTION")
    private MessageType messageType;

    //읽었는지
    @ApiModelProperty(required = true, value = "읽었는지 true or false", example = "true, false")
    private boolean isChecked;

    @ApiModelProperty(required = true, value = "알림 날짜", example = "2021-12-15T11:11:45.222207")
    private LocalDateTime createdDate;
}
