package com.example.boardapi.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString
public class MemberResponseDto {

    private String loginId;

    private String name;

    private int age;

    private String city;

    private String street;

    private String zipcode;
}
