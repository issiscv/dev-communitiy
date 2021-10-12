package com.example.boardapi.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@ApiModel(description = "로그인 요청 DTO")
@Getter @Setter
public class MemberLoginRequestDto {

    @ApiModelProperty(required = true, value = "ID", example = "abcd123")
    @NotBlank
    private String loginId;

    @ApiModelProperty(required = true, value = "PASSWORD", example = "qwer123")
    @NotBlank
    private String password;
}
