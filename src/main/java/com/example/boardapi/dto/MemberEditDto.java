package com.example.boardapi.dto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberEditDto {

    private String password;

    private String name;

    private int age;

    private String city;

    private String street;

    private String zipcode;
}
