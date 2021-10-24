package com.example.boardapi.dto.member.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
@ApiModel(description = "로그인 응답 DTO")
public class MemberLoginResponseDto {

    @ApiModelProperty(required = true, value = "회원의 토큰")
    private String token;

    @ApiModelProperty(required = true, value = "회원의 PK")
    private Long memberId;
}
