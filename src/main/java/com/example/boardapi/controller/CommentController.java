package com.example.boardapi.controller;

import com.example.boardapi.dto.comment.request.CommentEditRequestDto;
import com.example.boardapi.dto.comment.response.CommentEditResponseDto;
import com.example.boardapi.dto.comment.response.CommentRetrieveResponseDto;
import com.example.boardapi.config.jwt.JwtTokenProvider;
import com.example.boardapi.service.CommentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final JwtTokenProvider jwtTokenProvider;
    
    //단건 조회
    @GetMapping("/comments/{commentId}")
    public ResponseEntity retrieveComment(@PathVariable Long commentId) {

        CommentRetrieveResponseDto commentRetrieveResponseDto = commentService.retrieveOneWithDto(commentId);

        String ip = getIp();

        EntityModel<CommentRetrieveResponseDto> model = EntityModel.of(commentRetrieveResponseDto);
        WebMvcLinkBuilder self = linkTo(methodOn(this.getClass()).retrieveComment(commentId));
        WebMvcLinkBuilder boardLink = linkTo(methodOn(BoardController.class).retrieveBoard(commentRetrieveResponseDto.getBoardId()));

        model.add(self.withSelfRel());
        model.add(boardLink.withRel("게시글"));
        model.add(Link.of("http://"+ip+":8080/swagger-ui/#/", "profile"));

        return ResponseEntity.ok(commentRetrieveResponseDto);
    }

    //댓글 수정
    @ApiOperation(value = "게시글의 댓글 수정", notes = "게시글의 댓글을 수정합니다. CommentEditRequestDto DTO 를 사용합니다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "댓글 수정을 완료했습니다."),
            @ApiResponse(code = 400, message = "존재하지 않는 게시글 입니다. or 잘못된 요청 or 검증 실패"),
            @ApiResponse(code = 401, message = "토큰 검증 실패(인증 실패)")
    })
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<EntityModel<CommentEditResponseDto>> editComment(
            @ApiParam(value = "댓글 수정 DTO", required = true) @RequestBody @Valid CommentEditRequestDto commentEditRequestDto,
            @ApiParam(value = "댓글 PK", required = true) @PathVariable Long commentId,
            HttpServletRequest request) {

        String token = jwtTokenProvider.resolveToken(request);

        //댓글 수정
        CommentEditResponseDto commentEditResponseDto = commentService.editComment(commentId, commentEditRequestDto, token);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .build()
                .toUri();

        String ip = getIp();

        //hateoas 기능 추가
        EntityModel<CommentEditResponseDto> model = EntityModel.of(commentEditResponseDto);
        WebMvcLinkBuilder self = linkTo(methodOn(this.getClass()).editComment(commentEditRequestDto, commentId, request));

        //self
        model.add(self.withSelfRel());
        //profile
        model.add(Link.of("http://"+ip+":8080/swagger-ui/#/", "profile"));

        return ResponseEntity.created(uri).body(model);
    }

    //댓글 삭제
    @ApiOperation(value = "게시글의 댓글 삭제", notes = "게시글의 댓글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(code = 204, message = "댓글 삭제를 완료했습니다."),
            @ApiResponse(code = 400, message = "존재하지 않는 게시글 입니다."),
            @ApiResponse(code = 401, message = "토큰 검증 실패(인증 실패)"),
    })
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity deleteComment(
                                        @ApiParam(value = "댓글 PK", required = true) @PathVariable Long commentId,
                                        HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);

        //삭제
        commentService.deleteComment(commentId, token);

        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "게시글의 댓글 좋아요", notes = "게시글의 댓글을 좋아합니다..")
    @ApiResponses({
            @ApiResponse(code = 204, message = "댓글 좋아요를 정상적으로 수행했습니다."),
            @ApiResponse(code = 400, message = "존재하지 않는 게시글 입니다."),
            @ApiResponse(code = 401, message = "토큰 검증 실패(인증 실패)"),
    })
    @PutMapping("/comments/{commentId}/likes")
    public ResponseEntity updateCommentLike(
                                            @ApiParam(value = "댓글 PK", required = true) @PathVariable Long commentId
            ,HttpServletRequest request) {
        //게시글이 존재하는지 검사

        String token = jwtTokenProvider.resolveToken(request);

        //댓글이 존재하는지 같이 검사한다.
        commentService.updateCommentLike(commentId, token);

        return ResponseEntity.noContent().build();
    }

    //댓글 채택: 채택 수정, 채택 취소 안됨
    @ApiOperation(value = "게시글의 댓글 채택", notes = "게시글의 댓글을 채택합니다.")
    @ApiResponses({
            @ApiResponse(code = 204, message = "댓글 채택 정상적으로 수행했습니다."),
            @ApiResponse(code = 400, message = "존재하지 않는 댓글 입니다."),
            @ApiResponse(code = 401, message = "토큰 검증 실패(인증 실패)"),
    })
    @PutMapping("/comments/{commentId}/selections")
    public ResponseEntity selectComment(
            @ApiParam(value = "댓글 PK", required = true) @PathVariable Long commentId, HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        commentService.selectComment(commentId, token);

        return ResponseEntity.noContent().build();
    }

    private String getIp() {
        String ip = "";
        try {
            InetAddress local = InetAddress.getLocalHost();
            ip = local.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ip;
    }
}
