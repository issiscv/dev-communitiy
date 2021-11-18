package com.example.boardapi.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Comment {

    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    private String content;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

    private int likes;

    private boolean isSelected;

    public void setLikes(int like) {
        this.likes = like;
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void changeMember(Member member) {
        this.member = member;
    }

    public void changeBoard(Board board) {
        this.board = board;
    }

    public void chooseSelection(boolean selected) {
        this.isSelected = selected;
    }
}
