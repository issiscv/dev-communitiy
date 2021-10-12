package com.example.boardapi.controller;

import com.example.boardapi.domain.Board;
import com.example.boardapi.domain.Member;
import com.example.boardapi.dto.board.request.BoardCreateRequestDto;
import com.example.boardapi.dto.board.response.BoardCreateResponseDto;
import com.example.boardapi.repository.BoardRepository;
import com.example.boardapi.securityConfig.JWT.JwtTokenProvider;
import com.example.boardapi.service.BoardService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    private final JwtTokenProvider jwtTokenProvider;

    private final ModelMapper modelMapper;

    //작성 POST
    @PostMapping("/boards")
    public ResponseEntity createBoard(@RequestBody BoardCreateRequestDto boardCreateRequestDto,
                                      HttpServletRequest request) {
        //request 헤더 값을 가져와, 회원 조회
        String token = jwtTokenProvider.resolveToken(request);
        Member member = jwtTokenProvider.getMember(token);

        //DTO 를 Board 엔티티로 매핑 하고 저장
        Board mappedBoard = modelMapper.map(boardCreateRequestDto, Board.class);
        mappedBoard.setMember(member);
        Board savedBoard = boardService.save(mappedBoard);

        //응답 DTO
        BoardCreateResponseDto boardCreateResponseDto = modelMapper.map(savedBoard, BoardCreateResponseDto.class);
        boardCreateResponseDto.setAuthor(member.getName());

        //데이터베이스에 생성하였기에 주소를 설정해준다 해준다.
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(savedBoard.getId()).toUri();

        return ResponseEntity.created(uri).body(boardCreateResponseDto);
    }

    //단건 조회 GET
    @GetMapping("/boards/{id}")
    public ResponseEntity retrieveBoard(@PathVariable Long id) {
        
        //해당 PK 에 해당하는 게시판 엔티티 조회
        Board board = boardService.retrieveOne(id);
        
        //게시판 작성 시 응답 DTO 로 변환(형식이 같아 재사용)
        BoardCreateResponseDto boardCreateResponseDto = modelMapper.map(board, BoardCreateResponseDto.class);
        //응답 시 필드 명이 author 이므로 따로 세팅한다.
        boardCreateResponseDto.setAuthor(board.getMember().getName());

        return ResponseEntity.ok().body(boardCreateResponseDto);
    }


    //전체 조회 GET

    //수정 PUT
    
    //삭제 DELETE
}
