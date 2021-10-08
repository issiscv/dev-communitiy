package com.example.boardapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorController {

    @ExceptionHandler
    public ResponseEntity userNotFoundExceptionHandler(UserNotFoundException ex, WebRequest request) {
        log.info("exception = {}", ex.getMessage());
        ErrorResult errorResult =
                new ErrorResult(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity BadCredentialsExceptionHandler(BadCredentialsException ex, WebRequest request) {
        log.info("exception = {}", ex.getMessage());
        ErrorResult errorResult =
                new ErrorResult(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity DuplicateLoginIdExceptionHandler(DuplicateLoginIdException ex, WebRequest request) {
        ErrorResult errorResult =
                new ErrorResult(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity(errorResult, HttpStatus.BAD_REQUEST);
    }
}
