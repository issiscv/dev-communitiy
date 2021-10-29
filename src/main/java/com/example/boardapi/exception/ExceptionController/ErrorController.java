package com.example.boardapi.exception.ExceptionController;

import com.example.boardapi.exception.exception.*;
import com.example.boardapi.exception.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorController extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity notValidQueryStringExceptionHandler(NotValidQueryStringException ex, WebRequest request) {
        log.info("exception = {}", ex.getMessage());
        ErrorResult errorResult =
                new ErrorResult(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity(errorResult, HttpStatus.BAD_REQUEST);
    }

    //사용자를 못찾았을 때
    @ExceptionHandler
    public ResponseEntity userNotFoundExceptionHandler(UserNotFoundException ex, WebRequest request) {
        log.info("exception = {}", ex.getMessage());
        ErrorResult errorResult =
                new ErrorResult(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity(errorResult, HttpStatus.BAD_REQUEST);
    }
    
    //비밀번호가 틀렸을 때
    @ExceptionHandler
    public ResponseEntity BadCredentialsExceptionHandler(BadCredentialsException ex, WebRequest request) {
        log.info("exception = {}", ex.getMessage());
        ErrorResult errorResult =
                new ErrorResult(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity(errorResult, HttpStatus.BAD_REQUEST);
    }

    //회원 가입시 아이디가 중복일 때
    @ExceptionHandler
    public ResponseEntity DuplicateLoginIdExceptionHandler(DuplicateLoginIdException ex, WebRequest request) {
        ErrorResult errorResult =
                new ErrorResult(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity(errorResult, HttpStatus.BAD_REQUEST);
    }

    //게시글을 찾지 못하였을 때
    @ExceptionHandler
    public ResponseEntity boardNotFoundExceptionHandler(BoardNotFoundException ex, WebRequest request) {
        ErrorResult errorResult =
                new ErrorResult(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity(errorResult, HttpStatus.BAD_REQUEST);
    }

    //댓글을 찾지 못하였을 때
    @ExceptionHandler
    public ResponseEntity commentNotFoundExceptionHandler(CommentNotFoundException ex, WebRequest request) {
        ErrorResult errorResult =
                new ErrorResult(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity(errorResult, HttpStatus.BAD_REQUEST);
    }

    //수정, 삭제 하고자 하는 글이 나의 게시글이 아닐 때
    @ExceptionHandler
    public ResponseEntity notOwnBoardExceptionHandler(NotOwnBoardException ex, WebRequest request) {
        ErrorResult errorResult =
                new ErrorResult(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity(errorResult, HttpStatus.UNAUTHORIZED);
    }
    
    //검증 오류
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        ErrorResult errorResult =
                new ErrorResult(LocalDateTime.now(), "Validation Failed", ex.getBindingResult().toString());

        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }
}
