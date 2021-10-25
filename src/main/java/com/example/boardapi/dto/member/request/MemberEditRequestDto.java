package com.example.boardapi.dto.member.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(description = "회원 정보 변경 요청 DTO")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberEditRequestDto {

    @ApiModelProperty(required = true, value = "회원의 변경할 비밀번호", example = "qwer123")
    @NotBlank
    private String password;

    @ApiModelProperty(required = true, value = "회원의 변경할 이름", example = "홍길동")
    @NotBlank
    private String name;

    @ApiModelProperty(required = true, value = "회원의 변경할 나이", example = "24")
    @NotNull
    private int age;

    @ApiModelProperty(required = true, value = "회원의 변경할 주소", example = "서울 강남구 테헤란로")
    @NotBlank
    private String address;
}
