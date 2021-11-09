package com.example.boardapi.dto.board.response;

import com.example.boardapi.dto.comment.response.CommentCreateResponseDto;
import com.example.boardapi.dto.comment.response.CommentRetrieveResponseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@ApiModel(description = "게시글 자세한 조회(댓글 까지)")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class BoardRetrieveDetailResponseDto {

    @ApiModelProperty(required = true, value = "게시글 PK", example = "2")
    private Long id;

    @ApiModelProperty(required = true, value = "회원 PK", example = "1")
    private Long memberId;

    @ApiModelProperty(required = true, value = "게시글 작성자", example = "김상운")
    private String author;

    @ApiModelProperty(required = true, value = "게시글 제목", example = "안녕하세요!")
    private String title;

    @ApiModelProperty(required = true, value = "게시글 내용", example = "처음으로 작성합니다.")
    private String content;

    @ApiModelProperty(required = true, value = "게시글 작성 시간")
    private LocalDateTime createdDate;

    @ApiModelProperty(required = true, value = "게시글 수정 시간")
    private LocalDateTime lastModifiedDate;

    @ApiModelProperty(required = true, value = "게시글의 댓글 들")
    private List<CommentRetrieveResponseDto> comments;

    @ApiModelProperty(required = true, value = "좋아요")
    private int likes;

    @ApiModelProperty(required = true, value = "조회 수")
    private int views;

    @ApiModelProperty(required = true, value = "댓글 수")
    private int commentSize;

}
