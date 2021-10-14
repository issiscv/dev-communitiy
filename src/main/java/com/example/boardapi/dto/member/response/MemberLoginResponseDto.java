package com.example.boardapi.dto.member.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class MemberLoginResponseDto {

    private String token;

    private Long memberId;
}
