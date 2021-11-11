package com.example.boardapi.dto.member.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ApiModel(description = "회원 조회 응답 DTO")
public class MemberRetrieveResponseDto {

    @ApiModelProperty(required = true, value = "회원 아이디", example = "jisoo")
    private String loginId;

    @ApiModelProperty(required = true, value = "회원 이름", example = "홍길동")
    private String name;

    @ApiModelProperty(required = true, value = "회원 나이", example = "24")
    private int age;

    @ApiModelProperty(required = true, value = "회원의 주소", example = "서울 강남구 테헤란로")
    private String address;

    @ApiModelProperty(required = true, value = "회원의 활동 점수", example = "8002")
    private String activeScore;
}
