package com.example.boardapi.dto;

import com.example.boardapi.domain.Address;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class MemberRequestDto {

    private String loginId;

    private String password;

    private String name;

    private int age;

    private String city;

    private String street;

    private String zipcode;
}
