package com.example.boardapi.entity;

import com.example.boardapi.entity.enumtype.BoardType;
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
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Board {

    @Id @GeneratedValue
    @Column(name = "board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    private int views;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

    private int likes;

    private int commentSize;

    private boolean isSelected;

    public Board(Member member, String content, String title) {
        this.member = member;
        this.content = content;
        this.title = title;
    }

    public void changeLike(int like) {
        this.likes = like;
    }

    public void changeMember(Member member) {
        this.member = member;
    }

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void changeBoardType(BoardType boardType) {
        this.boardType = boardType;
    }

    public void increaseViews() {
        ++this.views;
    }

    public void increaseComments() {
        ++this.commentSize;
    }

    public void decreaseComments() {
        --this.commentSize;
    }

    public void chooseSelection(boolean selected) {
        this.isSelected = selected;
    }
}
