package com.example.boardapi.controller;

import com.example.boardapi.domain.Board;
import com.example.boardapi.domain.Comment;
import com.example.boardapi.domain.Member;
import com.example.boardapi.dto.comment.request.CommentCreateRequestDto;
import com.example.boardapi.dto.comment.request.CommentEditRequestDto;
import com.example.boardapi.dto.comment.response.CommentCreateResponseDto;
import com.example.boardapi.security.JWT.JwtTokenProvider;
import com.example.boardapi.service.BoardService;
import com.example.boardapi.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class CommentController {

    /**
     * 댓글 작성은 board api 에서 사용한다. 게시글에 의존적이기 때문이다.
     * 특정 게시글의 댓글 조회도 board api 에서 사용한다. 게시글에 의존적이기 때문이다.
     */
}
