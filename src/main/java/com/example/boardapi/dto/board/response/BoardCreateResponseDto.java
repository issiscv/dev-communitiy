package com.example.boardapi.dto.board.response;

import com.example.boardapi.domain.Member;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

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
