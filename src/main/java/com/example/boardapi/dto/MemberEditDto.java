package com.example.boardapi.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberEditDto {

    @ApiModelProperty(required = true, value = "회원의 변경할 비밀번호", example = "qwer123")
    private String password;

    @ApiModelProperty(required = true, value = "회원의 변경할 이름", example = "홍길동")
    private String name;

    @ApiModelProperty(required = true, value = "회원의 변경할 나이", example = "24")
    private int age;

    @ApiModelProperty(required = true, value = "회원의 변경할 도시", example = "서울")
    private String city;

    @ApiModelProperty(required = true, value = "회원의 변경할 거리", example = "강남구")
    private String street;

    @ApiModelProperty(required = true, value = "회원의 변경할 번지", example = "태헤란로")
    private String zipcode;
}
