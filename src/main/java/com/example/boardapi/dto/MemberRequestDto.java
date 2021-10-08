package com.example.boardapi.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@ApiModel(value = "MemberRequestDto", description = "회원 가입 DTO")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class MemberRequestDto {

    @ApiModelProperty(required = true, value = "회원 아이디", example = "jisoo")
    private String loginId;

    @ApiModelProperty(required = true, value = "회원 비밀번호", example = "qwer123")
    private String password;

    @ApiModelProperty(required = true, value = "회원 이름", example = "홍길동")
    private String name;

    @ApiModelProperty(required = true, value = "회원 나이", example = "24")
    private int age;

    @ApiModelProperty(required = true, value = "회원의 도시", example = "서울")
    private String city;

    @ApiModelProperty(required = true, value = "회원의 거리", example = "강남구")
    private String street;

    @ApiModelProperty(required = true, value = "회원의 번지", example = "태헤란로")
    private String zipcode;
}
