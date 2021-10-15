package com.example.boardapi.controller;

import com.example.boardapi.domain.Board;
import com.example.boardapi.domain.Comment;
import com.example.boardapi.domain.Member;
import com.example.boardapi.dto.board.request.BoardCreateRequestDto;
import com.example.boardapi.dto.board.request.BoardEditRequestDto;
import com.example.boardapi.dto.board.response.BoardCreateResponseDto;
import com.example.boardapi.dto.board.response.BoardRetrieveResponseDto;
import com.example.boardapi.dto.comment.request.CommentCreateRequestDto;
import com.example.boardapi.dto.comment.request.CommentEditRequestDto;
import com.example.boardapi.dto.comment.response.CommentCreateResponseDto;
import com.example.boardapi.dto.comment.response.CommentRetrieveResponseDto;
import com.example.boardapi.security.JWT.JwtTokenProvider;
import com.example.boardapi.service.BoardService;
import com.example.boardapi.service.CommentService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
@Slf4j
public class BoardController {

    private final BoardService boardService;

    private final JwtTokenProvider jwtTokenProvider;

    private final ModelMapper modelMapper;

    private final CommentService commentService;

    //작성 POST
    @ApiOperation(value = "게시글 작성", notes = "BoardCreateRequestDto DTO 를 통해 게시글을 생성합니다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "게시글 생성 성공"),
            @ApiResponse(code = 401, message = "토큰 검증 실패"),
            @ApiResponse(code = 403, message = "검증이 실패하였습니다.")
    })
    @PostMapping("")
    public ResponseEntity<BoardCreateResponseDto> createBoard(@ApiParam(value = "게시글 생성 DTO", required = true) @RequestBody @Valid BoardCreateRequestDto boardCreateRequestDto,
                                      HttpServletRequest request) {
        //request 헤더 값을 가져와, 회원 조회 : 누가 작성했는지 알기 위해서
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
                .path("/{id}")
                .buildAndExpand(savedBoard.getId()).toUri();

        return ResponseEntity.created(uri).body(boardCreateResponseDto);
    }

    //단건 조회 GET
    @ApiOperation(value = "게시글 단건 조회", notes = "게시글 엔티티의 PK를 경로 변수에 넣어 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "게시글 조회 성공"),
            @ApiResponse(code = 401, message = "토큰 검증 실패"),
            @ApiResponse(code = 400, message = "존재하지 않는 게시글입니다.")
    })
    @GetMapping("/{id}")
    public ResponseEntity retrieveBoard(@ApiParam(value = "게시글 PK", required = true) @PathVariable Long id) {
        
        //해당 PK 에 해당하는 게시판 엔티티 조회
        Board board = boardService.retrieveOne(id);
        //게시글에 해당하는 댓글 리스트
        List<Comment> comments = commentService.retrieveOneByBoardId(id);
        List<CommentRetrieveResponseDto> commentResponseDtoList = new ArrayList<>();
        
        //조회한 댓글 엔티티를 DTO 로 변환
        for (Comment comment : comments) {

            CommentRetrieveResponseDto commentRetrieveResponseDto = CommentRetrieveResponseDto.builder()
                    .id(comment.getId())
                    .boardId(board.getId())
                    .author(comment.getMember().getName())
                    .content(comment.getContent())
                    .createdDate(comment.getCreatedDate())
                    .lastModifiedDate(comment.getLastModifiedDate())
                    .build();

            commentResponseDtoList.add(commentRetrieveResponseDto);
        }

        //게시판 조회 시 해당 DTO 로 변환
        BoardRetrieveResponseDto boardRetrieveResponseDto = modelMapper.map(board, BoardRetrieveResponseDto.class);
        //응답 시 필드 명이 author 이므로 따로 세팅한다.
        boardRetrieveResponseDto.setAuthor(board.getMember().getName());
        boardRetrieveResponseDto.setComments(commentResponseDtoList);
        return ResponseEntity.ok().body(boardRetrieveResponseDto);
    }

    //전체 조회 GET
    @ApiOperation(value = "게시글 전체 조회", notes = "게시글 엔티티의 PK를 경로 변수에 넣어 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "게시글 전체 조회 성공"),
            @ApiResponse(code = 401, message = "토큰 검증 실패"),
    })
    @GetMapping("")
    public ResponseEntity retrieveAllBoard() {

        List<Board> boards = boardService.retrieveAll();

        //회원가입 응답 DTO 를 재사용
        //fetch 조인 필요
        List<BoardCreateResponseDto> boardCreateResponseDtoList = boards.stream().map(board -> {
            BoardCreateResponseDto boardCreateResponseDto = modelMapper.map(board, BoardCreateResponseDto.class);
            boardCreateResponseDto.setAuthor(board.getMember().getName());
            return boardCreateResponseDto;
                }
        ).collect(Collectors.toList());

        return ResponseEntity.ok().body(boardCreateResponseDtoList);
    }
    
    //수정 PUT
    @ApiOperation(value = "게시글 수정", notes = "게시글을 수정합니다. BoardEditRequestDto DTO 를 사용합니다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "게시글이 수정되었습니다."),
            @ApiResponse(code = 400, message = "존재하지 않는 게시글 입니다."),
            @ApiResponse(code = 401, message = "토큰 검증 실패"),
            @ApiResponse(code = 403, message = "검증이 실패하였습니다.")
    })
    @PutMapping("/{id}")
    public ResponseEntity editBoard(@ApiParam(value = "게시글 수정 DTO", required = true) @RequestBody BoardEditRequestDto boardEditRequestDto, @PathVariable Long id) {

        Board board = boardService.editBoard(id, boardEditRequestDto);

        BoardCreateResponseDto boardCreateResponseDto = modelMapper.map(board, BoardCreateResponseDto.class);
        boardCreateResponseDto.setAuthor(board.getMember().getName());

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(board.getId()).toUri();

        return ResponseEntity.created(uri).body(boardCreateResponseDto);
    }

    /**
     * 수정 필요
     */
    //삭제 DELETE
    @ApiOperation(value = "게시글 삭제", notes = "게시글 엔티티의 PK를 경로 변수에 넣어 삭제합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "게시글이 삭제되었습니다.."),
            @ApiResponse(code = 400, message = "존재하지 않는 게시글 입니다."),
            @ApiResponse(code = 401, message = "토큰 검증 실패"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);

        return ResponseEntity.ok().body("게시글 삭제 완료");
    }


    /**
     * 댓글 관련 API
     */
    //특정 게시판에 댓글을 쓰는 API
    @PostMapping("/{boardId}/comments")
    public ResponseEntity createComment(@RequestBody CommentCreateRequestDto commentCreateRequestDto,
                                        @PathVariable Long boardId, HttpServletRequest request) {

        //글쓴이의 정보(토큰의 정보)
        String token = jwtTokenProvider.resolveToken(request);
        Member member = jwtTokenProvider.getMember(token);

        //게시글 엔티티 조회
        Board board = boardService.retrieveOne(boardId);

        //DTO 를 변환 엔티티로 변환
        Comment comment = modelMapper.map(commentCreateRequestDto, Comment.class);
        comment.setBoard(board);
        comment.setMember(member);
        Comment saveComment = commentService.save(comment);

        //엔티티를 DTO로 변환
        CommentCreateResponseDto commentResponseDto = modelMapper.map(saveComment, CommentCreateResponseDto.class);
        commentResponseDto.setAuthor(member.getName());//작성자
        commentResponseDto.setId(saveComment.getId());//댓글 기본키
        commentResponseDto.setBoardId(board.getId());//게시글 기본키

        //URI
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saveComment.getId())
                .toUri();

        return ResponseEntity.created(uri).body(commentResponseDto);
    }

    //댓글 수정
    @PutMapping("/{boardId}/comments/{commentId}")
    public ResponseEntity editComment(@RequestBody CommentEditRequestDto commentEditRequestDto, @PathVariable Long boardId,
                                      @PathVariable Long commentId) {
        Comment comment = commentService.editComment(commentId, commentEditRequestDto);

        CommentCreateResponseDto commentCreateResponseDto = modelMapper.map(comment, CommentCreateResponseDto.class);
        commentCreateResponseDto.setAuthor(comment.getMember().getName());
        commentCreateResponseDto.setBoardId(boardId);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .build()
                .toUri();

        return ResponseEntity.created(uri).body(commentCreateResponseDto);
    }
}
