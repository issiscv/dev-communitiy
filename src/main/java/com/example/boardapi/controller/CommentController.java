package com.example.boardapi.controller;

import com.example.boardapi.entity.Comment;
import com.example.boardapi.dto.comment.response.CommentRetrieveResponseDto;
import com.example.boardapi.service.CommentService;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/comments/{commentId}")
    public ResponseEntity retrieveComment(@PathVariable Long commentId) {

        Comment comment = commentService.retrieveOne(commentId);

        CommentRetrieveResponseDto commentRetrieveResponseDto = CommentRetrieveResponseDto.builder()
                .id(comment.getId())
                .memberId(comment.getMember().getId())
                .boardId(comment.getBoard().getId())
                .author(comment.getMember().getName())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .lastModifiedDate(comment.getLastModifiedDate())
                .likes(comment.getLikes())
                .build();

        String ip = getIp();

        EntityModel<CommentRetrieveResponseDto> model = EntityModel.of(commentRetrieveResponseDto);
        WebMvcLinkBuilder self = linkTo(methodOn(this.getClass()).retrieveComment(commentId));
        WebMvcLinkBuilder boardLink = linkTo(methodOn(BoardController.class).retrieveBoard(comment.getBoard().getId()));

        model.add(self.withSelfRel());
        model.add(boardLink.withRel("게시글"));
        model.add(Link.of("http://"+ip+":8080/swagger-ui/#/", "profile"));

        return ResponseEntity.ok(commentRetrieveResponseDto);
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
