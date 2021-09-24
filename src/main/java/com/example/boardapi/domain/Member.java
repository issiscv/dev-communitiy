package com.example.boardapi.domain;

import com.example.boardapi.dto.EditMemberDto;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@EntityListeners(AuditingEntityListener.class)
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String loginId;

    private String password;

    private String name;

    private int age;

    private String city;

    private String street;

    private String zipcode;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public void changeMemberInfo(EditMemberDto editMemberDto) {
        this.password = editMemberDto.getPassword();
        this.name = editMemberDto.getName();
        this.age = editMemberDto.getAge();
        this.city = editMemberDto.getCity();
        this.street = editMemberDto.getStreet();
        this.zipcode = editMemberDto.getZipcode();
    }
}
