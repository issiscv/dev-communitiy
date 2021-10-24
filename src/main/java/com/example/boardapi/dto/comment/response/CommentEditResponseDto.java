package com.example.boardapi.dto.comment.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

@ApiModel(description = "댓글 수정 응답 DTO")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentEditResponseDto {

    @ApiModelProperty(required = true, value = "댓글 PK", example = "23")
    private Long id;//댓글 기본키

    @ApiModelProperty(required = true, value = "해당 게시글의 PK", example = "12")
    private Long boardId;//게시글 기본키

    @ApiModelProperty(required = true, value = "댓글 작성자", example = "김상운")
    private String author;//댓글 작성

    @ApiModelProperty(required = true, value = "댓글 내용", example = "댓글1")
    private String content;

    @ApiModelProperty(required = true, value = "댓글 작성 시간")
    private LocalDateTime createdDate;

    @ApiModelProperty(required = true, value = "댓글 수정 시간")
    private LocalDateTime lastModifiedDate;
}
