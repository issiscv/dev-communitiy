package com.example.boardapi.entity;

import com.example.boardapi.entity.enumtype.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Notice {

    @Id @GeneratedValue
    @Column(name = "notice_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    //누가
    private String loginId;
    
    //메세지 유형(댓글 달림, 좋아요 눌림, 채택 당함)
    @Enumerated(EnumType.STRING)
    private MessageType messageType;
    
    //읽었는지
    private boolean isChecked;
}
