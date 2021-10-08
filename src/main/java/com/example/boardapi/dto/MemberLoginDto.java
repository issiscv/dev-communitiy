package com.example.boardapi.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel(value = "MemberLoginDto", description = "로그인 DTO")
@Getter @Setter
public class MemberLoginDto {

    @ApiModelProperty(required = true, value = "ID", example = "abcd123")
    private String loginId;

    @ApiModelProperty(required = true, value = "PASSWORD", example = "qwer123")
    private String password;
}
