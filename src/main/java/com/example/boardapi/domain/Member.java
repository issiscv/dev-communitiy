package com.example.boardapi.domain;

import com.example.boardapi.dto.request.MemberEditRequestDto;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@EntityListeners(AuditingEntityListener.class)
public class Member implements UserDetails {

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

    //값을 즉시 채워넣어야 하기 때문에
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    //권한을 가져오는 이 메서드는, 인증 객체를 만들어줄 때 필요하다.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //문자열 "ROLE_USER" 를 갖고 있는 List 를  SimpleGrantedAuthority 객체에 넣어서 List로 반환한다.
        //즉 provider 에서 권한을 갖고 올 수 있게 하기 위해서
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    //회원 수정 api 에서 사용됨
    public void changeMemberInfo(MemberEditRequestDto editMemberDto) {
        this.password = editMemberDto.getPassword();
        this.name = editMemberDto.getName();
        this.age = editMemberDto.getAge();
        this.city = editMemberDto.getCity();
        this.street = editMemberDto.getStreet();
        this.zipcode = editMemberDto.getZipcode();
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
