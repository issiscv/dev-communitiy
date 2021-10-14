package com.example.boardapi.dto.board.response;

import com.example.boardapi.dto.comment.response.CommentCreateResponseDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class BoardCreateResponseDto {

    private Long id;

    private String author;

    private String title;

    private String content;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;
}
