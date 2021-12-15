package com.example.boardapi.entity;

import com.example.boardapi.entity.enumtype.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
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
public class Notice {

    @Id @GeneratedValue
    @Column(name = "notice_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    private String title;

    //누가
    private String loginId;
    
    //메세지 유형(댓글 달림, 좋아요 눌림, 채택 당함)
    @Enumerated(EnumType.STRING)
    private MessageType messageType;
    
    //읽었는지
    private boolean isChecked;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public void changeChecked() {
        this.isChecked = true;
    }
}
