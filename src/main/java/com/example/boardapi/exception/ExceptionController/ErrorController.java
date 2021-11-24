package com.example.boardapi.exception.ExceptionController;

import com.example.boardapi.exception.*;
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
    public ResponseEntity inValidQueryStringExceptionHandler(InValidQueryStringException ex, WebRequest request) {
        log.info("exception = {}", ex.getMessage());
        ErrorResult errorResult =
                new ErrorResult(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity(errorResult, HttpStatus.BAD_REQUEST);
    }

    //사용자를 못찾았을 때
    @ExceptionHandler
    public ResponseEntity userNotFoundExceptionHandler(MemberNotFoundException ex, WebRequest request) {
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

    //수정, 삭제, 조회 하고자 하는 글이 나의 회원 정보가 아닐 때
    @ExceptionHandler
    public ResponseEntity notOwnMemberExceptionHandler(NotOwnMemberException ex, WebRequest request) {
        ErrorResult errorResult =
                new ErrorResult(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity(errorResult, HttpStatus.UNAUTHORIZED);
    }

    //댓글, 게시글에 중복으로 좋아요 누를때
    @ExceptionHandler
    public ResponseEntity DuplicatedLikeExceptionHandler(DuplicatedLikeException ex, WebRequest request) {
        ErrorResult errorResult =
                new ErrorResult(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity(errorResult, HttpStatus.BAD_REQUEST);
    }

    //채택을 중복으로 할 때
    @ExceptionHandler
    public ResponseEntity selectedCommentExistExceptionHandler(InvalidSelectionException ex, WebRequest request) {
        ErrorResult errorResult =
                new ErrorResult(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity(errorResult, HttpStatus.BAD_REQUEST);
    }

    //채택된 댓글 수정, 삭제할 때
    @ExceptionHandler
    public ResponseEntity notValidEditExceptionHandler(InValidUpdateException ex, WebRequest request) {
        ErrorResult errorResult =
                new ErrorResult(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity(errorResult, HttpStatus.BAD_REQUEST);
    }

    //입력 글자가 너무 짧을 때
    @ExceptionHandler
    public ResponseEntity shortInputExceptionHandler(ShortInputException ex, WebRequest request) {
        ErrorResult errorResult =
                new ErrorResult(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity(errorResult, HttpStatus.BAD_REQUEST);
    }

    //이미 스크랩한 경우
    @ExceptionHandler
    public ResponseEntity alreadyScrapedExceptionHandler(AlreadyScrapedException ex, WebRequest request) {
        ErrorResult errorResult =
                new ErrorResult(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity(errorResult, HttpStatus.BAD_REQUEST);
    }

    //댓글이 있는 게시글 삭제 경우
    @ExceptionHandler
    public ResponseEntity boardNotDeletedExceptionHandler(BoardNotDeletedException ex, WebRequest request) {
        ErrorResult errorResult =
                new ErrorResult(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity(errorResult, HttpStatus.BAD_REQUEST);
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
