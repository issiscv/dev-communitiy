package com.example.boardapi.dto.member.response;

import io.swagger.annotations.ApiModel;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ApiModel(description = "회원 가입 응답 DTO")
public class MemberJoinResponseDto {

    private Long id;

    private String loginId;

    private String name;

    private int age;

    private String city;

    private String street;

    private String zipcode;
}
