package com.example.boardapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloBoard {

    @GetMapping("/")
    public String hello() {
        return "hello";
    }
}
