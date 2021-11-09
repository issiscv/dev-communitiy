package com.example.boardapi.dto.comment.response;

import com.example.boardapi.domain.Board;
import com.example.boardapi.domain.Member;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@ApiModel(description = "댓글 작성 응답 DTO")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateResponseDto {

    @ApiModelProperty(required = true, value = "댓글 PK", example = "23")
    private Long id;//댓글 기본키

    @ApiModelProperty(required = true, value = "해당 게시글의 PK", example = "12")
    private Long boardId;//게시글 기본키

    @ApiModelProperty(required = true, value = "댓글 작성자 PK", example = "2")
    private Long memberId;//게시글 기본키

    @ApiModelProperty(required = true, value = "댓글 작성자", example = "김상운")
    private String author;//댓글 작성

    @ApiModelProperty(required = true, value = "댓글 내용", example = "댓글1")
    private String content;

    @ApiModelProperty(required = true, value = "댓글 작성 시간")
    private LocalDateTime createdDate;
    
    @ApiModelProperty(required = true, value = "댓글 수정 시간")
    private LocalDateTime lastModifiedDate;

    @ApiModelProperty(required = true, value = "댓글 좋아요 수")
    private int likes;
}
