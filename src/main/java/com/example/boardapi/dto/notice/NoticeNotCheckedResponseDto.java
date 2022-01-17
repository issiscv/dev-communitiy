package com.example.boardapi.dto.notice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@ApiModel(description = "안읽은 알림의 개수 DTO")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeNotCheckedResponseDto {


    @ApiModelProperty(required = true, value = "안읽은 알림 개수", example = "15")
    private Long count;
}
