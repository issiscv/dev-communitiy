package com.example.boardapi.dto.member.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ApiModel(description = "회원 가입 응답 DTO")
public class MemberJoinResponseDto {

    @ApiModelProperty(required = true, value = "회원 PK", example = "23")
    private Long id;

    @ApiModelProperty(required = true, value = "회원 아이디", example = "abcd123")
    private String loginId;

    @ApiModelProperty(required = true, value = "회원 이름", example = "jisoo")
    private String name;

    @ApiModelProperty(required = true, value = "회원 나이", example = "24")
    private int age;

    @ApiModelProperty(required = true, value = "회원의 주소", example = "서울 강남구 테헤란로")
    private String address;
}
