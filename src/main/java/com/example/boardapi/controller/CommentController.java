package com.example.boardapi.controller;

import com.example.boardapi.domain.Comment;
import com.example.boardapi.dto.comment.request.CommentEditRequestDto;
import com.example.boardapi.dto.comment.response.CommentCreateResponseDto;
import com.example.boardapi.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class CommentController {

    /**
     * 댓글 작성은 board api 에서 사용한다. 게시글에 의존적이기 때문이다.
     * 특정 게시글의 댓글 조회도 board api 에서 사용한다. 게시글에 의존적이기 때문이다.
     */

    private final CommentService commentService;
    private final ModelMapper modelMapper;



    //댓글 삭제
}
