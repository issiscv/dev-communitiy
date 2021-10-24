package com.example.boardapi.dto.member.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(description = "회원 가입 요청 DTO")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class MemberJoinRequestDto {

    @ApiModelProperty(required = true, value = "회원 아이디", example = "jisoo")
    @NotBlank
    private String loginId;

    @ApiModelProperty(required = true, value = "회원 비밀번호", example = "qwer123")
    @NotBlank
    private String password;

    @ApiModelProperty(required = true, value = "회원 이름", example = "홍길동")
    @NotBlank
    private String name;

    @ApiModelProperty(required = true, value = "회원 나이", example = "24")
    @NotNull
    private int age;

    @ApiModelProperty(required = true, value = "회원의 도시", example = "서울")
    @NotBlank
    private String city;

    @ApiModelProperty(required = true, value = "회원의 거리", example = "강남구")
    @NotBlank
    private String street;

    @ApiModelProperty(required = true, value = "회원의 번지", example = "태헤란로7가길 2")
    @NotBlank
    private String zipcode;
}
