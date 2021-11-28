package com.example.boardapi.controller;

import com.example.boardapi.dto.board.request.BoardCreateRequestDto;
import com.example.boardapi.dto.board.request.BoardEditRequestDto;
import com.example.boardapi.dto.board.response.*;
import com.example.boardapi.dto.comment.request.CommentCreateRequestDto;
import com.example.boardapi.dto.comment.request.CommentEditRequestDto;
import com.example.boardapi.dto.comment.response.CommentCreateResponseDto;
import com.example.boardapi.dto.comment.response.CommentEditResponseDto;
import com.example.boardapi.dto.comment.response.CommentRetrieveResponseDto;
import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.Comment;
import com.example.boardapi.entity.Member;
import com.example.boardapi.entity.Scrap;
import com.example.boardapi.entity.enumtype.BoardType;
import com.example.boardapi.entity.enumtype.SortType;
import com.example.boardapi.exception.AlreadyScrapedException;
import com.example.boardapi.exception.DuplicatedLikeException;
import com.example.boardapi.exception.NotOwnBoardException;
import com.example.boardapi.exception.ShortInputException;
import com.example.boardapi.security.JWT.JwtTokenProvider;
import com.example.boardapi.service.BoardService;
import com.example.boardapi.service.CommentService;
import com.example.boardapi.service.ScrapService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
@Slf4j
public class BoardController {

    private final BoardService boardService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ModelMapper modelMapper;
    private final CommentService commentService;
    private final ScrapService scrapService;

    //작성 POST
    @ApiOperation(value = "게시글 작성", notes = "BoardCreateRequestDto DTO 를 통해 게시글을 생성합니다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "게시글 생성 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청 or 검증 실패"),
            @ApiResponse(code = 401, message = "토큰 검증 실패(인증 실패)")
    })
    @PostMapping("")
    public ResponseEntity<EntityModel<BoardCreateResponseDto>> createBoard(@ApiParam(value = "게시글 생성 DTO", required = true) @RequestBody @Valid BoardCreateRequestDto boardCreateRequestDto,
                                                                           @ApiParam(value = "게시글 종류 쿼리 스트링", required = true, example = "tech, qna, free") @RequestParam(value = "type") BoardType boardType, HttpServletRequest request) {
        //게시글 저장 후 DTO 로 변환
        BoardCreateResponseDto boardCreateResponseDto = boardService.save(boardCreateRequestDto, boardType, request);

        //데이터베이스에 생성하였기에 주소를 설정해준다.
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(boardCreateResponseDto.getId()).toUri();

        //profile 주소를 hateoas에 추가를 위해 ip 주소를 가져온다.
        String ip = getIp();

        //hateoas 기능 추가
        EntityModel<BoardCreateResponseDto> model = EntityModel.of(boardCreateResponseDto);
        //self
        WebMvcLinkBuilder self = linkTo(methodOn(this.getClass()).createBoard(new BoardCreateRequestDto(), boardType, request));
        //단건 조회
        WebMvcLinkBuilder retrieve = linkTo(methodOn(this.getClass()).retrieveBoard(boardCreateResponseDto.getId()));

        model.add(self.withSelfRel());
        model.add(retrieve.withRel("게시글 조회"));
        model.add(Link.of("http://"+ip+":8080/swagger-ui/#/", "profile"));

        return ResponseEntity.created(uri).body(model);
    }

    //단건 조회 및 자세한 조회(댓글 까지) GET
    @ApiOperation(value = "게시글 단건 조회", notes = "게시글 엔티티의 PK를 경로 변수에 넣어 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "게시글 조회 성공"),
            @ApiResponse(code = 400, message = "존재하지 않는 게시글입니다."),
    })
    @GetMapping("/{boardId}")
    public ResponseEntity<EntityModel<BoardRetrieveDetailResponseDto>> retrieveBoard(@ApiParam(value = "게시글 PK", required = true) @PathVariable Long boardId) {
        //해당 PK 에 해당하는 게시판 엔티티 조회, 조회 수 증가
        BoardRetrieveDetailResponseDto boardRetrieveResponseDto = boardService.retrieveOneAndIncreaseViews(boardId);

        //게시글에 해당하는 댓글 리스트
        List<CommentRetrieveResponseDto> commentResponseDtoList = commentService.retrieveAllByBoardId(boardId);

        //게시글 조회 DTO 에 댓글 값이 세팅되있지 않아 세팅해준다.
        boardRetrieveResponseDto.setComments(commentResponseDtoList);

        //ip
        String ip = getIp();

        //hateoas 기능 추가
        EntityModel<BoardRetrieveDetailResponseDto> model = EntityModel.of(boardRetrieveResponseDto);
        WebMvcLinkBuilder self = linkTo(methodOn(this.getClass()).retrieveBoard(boardId));
        //self
        model.add(self.withSelfRel());
        model.add(Link.of("http://"+ip+":8080/swagger-ui/#/", "profile"));


        return ResponseEntity.ok().body(model);
    }

    //전체 조회 GET
    @ApiOperation(value = "게시글 전체 조회", notes = "쿼리스트링을 사용하여 게시글 종류를 구분하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "게시글 전체 조회 성공"),
            @ApiResponse(code = 400, message = "쿼리스트링을 잘못 입력하셨습니다.")
    })
    @GetMapping("")
    public ResponseEntity<EntityModel<BoardRetrieveAllPagingResponseDto>> retrieveAllBoardByType(
            @ApiParam(value = "페이징을 위한 쿼리 스트링", required = false) @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "게시글 종류 쿼리 스트링", required = true, example = "tech, qna, free") @RequestParam(value = "type") BoardType boardType,
            @ApiParam(value = "게시글 정렬 유형 쿼리스트링", required = false, example = "createdDate, likes, commentSize, views") @RequestParam(value = "sort", defaultValue = "createdDate") SortType sortType) {

        //페이징 방식 대로 조회
        BoardRetrieveAllPagingResponseDto boardRetrieveAllPagingResponseDto = boardService.retrieveAllWithPagingByType(page, boardType, sortType);
        int totalPages = boardRetrieveAllPagingResponseDto.getTotalPages();

        //ip
        String ip = getIp();

        //hateoas 기능 추가
        EntityModel<BoardRetrieveAllPagingResponseDto> model = EntityModel.of(boardRetrieveAllPagingResponseDto);
        WebMvcLinkBuilder self = linkTo(methodOn(this.getClass()).retrieveAllBoardByType(page, boardType, sortType));
        //self
        model.add(self.withSelfRel());
        model.add(Link.of("http://"+ip+":8080/swagger-ui/#/", "profile"));

        //페이징 hateoas 를 위한 로직이다.
        if (page > 1) {
            WebMvcLinkBuilder prev = linkTo(methodOn(this.getClass()).retrieveAllBoardByType(page - 1, boardType, sortType));
            model.add(prev.withRel("이전"));
        }
        if (page < totalPages) {
            WebMvcLinkBuilder next = linkTo(methodOn(this.getClass()).retrieveAllBoardByType(page + 1, boardType, sortType));
            model.add(next.withRel("다음"));
        }

        return ResponseEntity.ok().body(model);
    }

    //검색 GET
    @ApiOperation(value = "게시글 검색", notes = "keyWord 에 해당하는 title or content 를 검색합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "게시글 검색 성공"),
            @ApiResponse(code = 400, message = "글자를 2글자 이상 입력해주세요")
    })
    @GetMapping("/v2")
    public ResponseEntity<EntityModel<BoardRetrieveAllPagingResponseDto>> retrieveAllBoardByKeyWord(
            @ApiParam(value = "페이징을 위한 쿼리 스트링", required = false) @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "검색 조건 위한 쿼리 스트링", required = true) @RequestParam String searchCond,
            @ApiParam(value = "검색을 위한 쿼리 스트링") @RequestParam(defaultValue = "") String keyWord,
            @ApiParam(value = "게시글 종류 쿼리 스트링", required = true, example = "tech, qna, free") @RequestParam String type) {

        if (keyWord.length() < 2) {
            throw new ShortInputException("2글자 이상 입력해주세요.");
        }

        //페이징 기준
        PageRequest pageRequest = PageRequest.of(page-1, 15);
        //페이징 방식 대로 조회
        Page<Board> boardPage = boardService.retrieveAllWithPagingByKeyWord(pageRequest, searchCond, keyWord, type);

        long totalElements = boardPage.getTotalElements();

        //총 페이지 수
        int totalPages = boardPage.getTotalPages();
        //해당 페이지의 컨텐트들
        List<Board> content = boardPage.getContent();

        List<BoardRetrieveResponseDto> boardRetrieveOneResponseDtoList = content.stream().map(board -> {
                    BoardRetrieveResponseDto boardRetrieveOneResponseDto = modelMapper.map(board, BoardRetrieveResponseDto.class);
                    boardRetrieveOneResponseDto.setAuthor(board.getMember().getName());
                    return boardRetrieveOneResponseDto;
                }
        ).collect(Collectors.toList());

        BoardRetrieveAllPagingResponseDto boardRetrieveAllPagingResponseDto =
                new BoardRetrieveAllPagingResponseDto(page, totalPages, (int)totalElements, boardRetrieveOneResponseDtoList);

        //ip
        String ip = getIp();

        //hateoas 기능 추가
        EntityModel<BoardRetrieveAllPagingResponseDto> model = EntityModel.of(boardRetrieveAllPagingResponseDto);
        WebMvcLinkBuilder self = linkTo(methodOn(this.getClass()).retrieveAllBoardByKeyWord(page, searchCond, keyWord, type));
        //self
        model.add(self.withSelfRel());
        model.add(Link.of("http://"+ip+":8080/swagger-ui/#/", "profile"));

        //페이징 hateoas 를 위한 로직이다.
        if (page > 1) {
            WebMvcLinkBuilder prev = linkTo(methodOn(this.getClass()).retrieveAllBoardByKeyWord(page - 1, searchCond, keyWord, type));
            model.add(prev.withRel("이전"));
        }
        if (page < totalPages) {
            WebMvcLinkBuilder next = linkTo(methodOn(this.getClass()).retrieveAllBoardByKeyWord(page + 1, searchCond, keyWord, type));
            model.add(next.withRel("다음"));
        }

        return ResponseEntity.ok().body(model);
    }

    //주간 best 게시글 조회 GET
    @ApiOperation(value = "게시글 1주 내 best 게시글", notes = "쿼리스트링을 활용하여 1 주 내의 best 게시글을 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "게시글 전체 조회 성공"),
            @ApiResponse(code = 400, message = "쿼리스트링을 잘못 입력하셨습니다.")
    })
    @GetMapping("/best-likes")
    public ResponseEntity<EntityModel<BoardRetrieveAllByWeekResponseDto>> retrieveAllBoardWeeklyBestByType() {

        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<Board> page = boardService.retrieveByTypeAndWeeklyBestBoardsWithPaging(pageRequest);

        List<Board> content = page.getContent();

        List<BoardRetrieveResponseDto> boardRetrieveOneResponseDtoList = content.stream().map(board -> {
                    BoardRetrieveResponseDto boardRetrieveOneResponseDto = modelMapper.map(board, BoardRetrieveResponseDto.class);
                    boardRetrieveOneResponseDto.setAuthor(board.getMember().getName());
                    return boardRetrieveOneResponseDto;
                }
        ).collect(Collectors.toList());

        BoardRetrieveAllByWeekResponseDto boardRetrieveAllByDateResponseDto = new BoardRetrieveAllByWeekResponseDto(boardRetrieveOneResponseDtoList);

        //ip
        String ip = getIp();

        //hateoas 기능 추가
        EntityModel<BoardRetrieveAllByWeekResponseDto> model = EntityModel.of(boardRetrieveAllByDateResponseDto);

        WebMvcLinkBuilder self = linkTo(methodOn(this.getClass()).retrieveAllBoardWeeklyBestByType());

        //self
        model.add(self.withSelfRel());
        model.add(Link.of("http://"+ip+":8080/swagger-ui/#/", "profile"));

        //페이징 hateoas 를 위한 로직이다.

        return ResponseEntity.ok().body(model);
    }

    //스크랩
    @ApiOperation(value = "게시글 스크랩", notes = "게시글을 스크랩하여 저장합니다.")
    @ApiResponses({
            @ApiResponse(code = 204, message = "스크랩 성공"),
            @ApiResponse(code = 400, message = "존재하지 않는 게시글 입니다."),
            @ApiResponse(code = 401, message = "토큰 검증 실패(인증 실패)")
    })
    @PutMapping("/{boardId}/scraps")
    public ResponseEntity scrapBoard(@ApiParam(value = "게시글 PK", required = true) @PathVariable Long boardId, HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        Member member = jwtTokenProvider.retrieveMember(token);

        Board board = boardService.retrieveOne(boardId);

        List<Scrap> scraps = scrapService.retrieveByMemberId(member.getId());
        for (Scrap scrap : scraps) {
            if (scrap.getBoard() == board) {
                throw new AlreadyScrapedException("이미 스크랩 하셨습니다.");
            }
        }

        Scrap scrap = Scrap.builder()
                .board(board)
                .member(member)
                .build();

        scrapService.save(scrap);

        return ResponseEntity.noContent().build();
    }

    //수정 PUT
    @ApiOperation(value = "게시글 수정", notes = "게시글을 수정합니다. BoardEditRequestDto DTO 를 사용합니다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "게시글이 수정되었습니다."),
            @ApiResponse(code = 400, message = "존재하지 않는 게시글 입니다. or 잘못된 요청 or 검증 실패"),
            @ApiResponse(code = 401, message = "토큰 검증 실패(인증 실패)")
    })
    @PutMapping("/{boardId}")
    public ResponseEntity<EntityModel<BoardEditResponseDto>> editBoard(@ApiParam(value = "게시글 수정 DTO", required = true) @RequestBody @Valid
                                                BoardEditRequestDto boardEditRequestDto,
                                    @ApiParam(value = "게시판 PK", required = true) @PathVariable Long boardId, HttpServletRequest request) {

        Board editBoard = boardService.editBoard(boardId, boardEditRequestDto, request);

        BoardEditResponseDto boardEditResponseDto = modelMapper.map(editBoard, BoardEditResponseDto.class);
        boardEditResponseDto.setAuthor(editBoard.getMember().getName());

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(editBoard.getId()).toUri();

        //ip
        String ip = getIp();

        //hateoas 기능 추가
        EntityModel<BoardEditResponseDto> model = EntityModel.of(boardEditResponseDto);
        WebMvcLinkBuilder self = linkTo(methodOn(this.getClass()).editBoard(boardEditRequestDto, boardId, request));
        //self
        model.add(self.withSelfRel());
        model.add(Link.of("http://"+ip+":8080/swagger-ui/#/", "profile"));

        return ResponseEntity.created(uri).body(model);
    }


    //삭제 DELETE
    @ApiOperation(value = "게시글 삭제", notes = "게시글 엔티티의 PK를 경로 변수에 넣어 삭제합니다.")
    @ApiResponses({
            @ApiResponse(code = 204, message = "게시글이 삭제되었습니다.."),
            @ApiResponse(code = 400, message = "존재하지 않는 게시글 입니다."),
            @ApiResponse(code = 401, message = "토큰 검증 실패(인증 실패)")
    })
    @DeleteMapping("/{boardId}")
    public ResponseEntity deleteBoard(@ApiParam(value = "게시판 PK", required = true) @PathVariable Long boardId, HttpServletRequest request) {

        boardService.deleteBoard(boardId, request);

        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "게시글의 좋아요", notes = "게시글을 좋아합니다.")
    @ApiResponses({
            @ApiResponse(code = 204, message = "게시글 좋아요를 정상적으로 수행했습니다."),
            @ApiResponse(code = 400, message = "존재하지 않는 게시글 입니다."),
            @ApiResponse(code = 401, message = "토큰 검증 실패(인증 실패)"),
    })
    @PutMapping("/{boardId}/likes")
    public ResponseEntity updateLike(@ApiParam(value = "게시판 PK", required = true) @PathVariable Long boardId,
                                     HttpServletRequest request) {

        boardService.updateBoardLike(boardId, request);

        return ResponseEntity.noContent().build();
    }

    /**
     * 이 밑으로는 댓글 API
     */
    //특정 게시판에 댓글을 쓰는 API
    @ApiOperation(value = "게시글의 댓글 작성", notes = "게시글의 댓글을 추가합니다. CommentCreateRequestDto DTO 를 사용합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "댓글 작성을 완료했습니다."),
            @ApiResponse(code = 400, message = "존재하지 않는 게시글 입니다. or 잘못된 요청 or 검증 실패"),
            @ApiResponse(code = 401, message = "토큰 검증 실패(인증 실패)")
    })
    @PostMapping("/{boardId}/comments")
    public ResponseEntity<EntityModel<CommentCreateResponseDto>> createComment(@RequestBody @Valid @ApiParam(value = "댓글 DTO", required = true) CommentCreateRequestDto commentCreateRequestDto,
                                        @ApiParam(value = "게시판 PK", required = true) @PathVariable Long boardId, HttpServletRequest request) {

        CommentCreateResponseDto commentResponseDto = commentService.save(boardId, commentCreateRequestDto, request);

        //URI
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(commentResponseDto.getId())
                .toUri();
        //ip
        String ip = getIp();

        //hateoas 기능 추가
        EntityModel<CommentCreateResponseDto> model = EntityModel.of(commentResponseDto);
        WebMvcLinkBuilder self = linkTo(methodOn(this.getClass()).createComment(commentCreateRequestDto, boardId, request));
        WebMvcLinkBuilder boardLink = linkTo(methodOn(this.getClass()).retrieveBoard(boardId));

        //self
        model.add(self.withSelfRel());
        //profile
        model.add(Link.of("http://"+ip+":8080/swagger-ui/#/", "profile"));
        //게시글
        model.add(boardLink.withRel("게시글"));
        return ResponseEntity.created(uri).body(model);
    }

    //댓글 수정
    @ApiOperation(value = "게시글의 댓글 수정", notes = "게시글의 댓글을 수정합니다. CommentEditRequestDto DTO 를 사용합니다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "댓글 수정을 완료했습니다."),
            @ApiResponse(code = 400, message = "존재하지 않는 게시글 입니다. or 잘못된 요청 or 검증 실패"),
            @ApiResponse(code = 401, message = "토큰 검증 실패(인증 실패)")
    })
    @PutMapping("/{boardId}/comments/{commentId}")
    public ResponseEntity<EntityModel<CommentEditResponseDto>> editComment(@ApiParam(value = "댓글 수정 DTO", required = true) @RequestBody @Valid CommentEditRequestDto commentEditRequestDto,
                                      @ApiParam(value = "게시판 PK", required = true) @PathVariable Long boardId,
                                      @ApiParam(value = "댓글 PK", required = true) @PathVariable Long commentId,
                                                                           HttpServletRequest request) {
        //게시글이 존재하는지 검사
        Board board = boardService.retrieveOne(boardId);
        //댓글 수정
        Comment comment = commentService.editComment(commentId, commentEditRequestDto);


        String token = jwtTokenProvider.resolveToken(request);
        Member member = jwtTokenProvider.retrieveMember(token);

        if (comment.getMember().getId() != member.getId()) {
            throw new NotOwnBoardException("게시글의 권한이 없습니다.");
        }

        CommentEditResponseDto commentEditResponseDto = modelMapper.map(comment, CommentEditResponseDto.class);
        commentEditResponseDto.setAuthor(comment.getMember().getName());
        commentEditResponseDto.setBoardId(boardId);
        commentEditResponseDto.setMemberId(member.getId());
        commentEditResponseDto.setSelected(comment.isSelected());

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .build()
                .toUri();

        String ip = getIp();

        //hateoas 기능 추가
        EntityModel<CommentEditResponseDto> model = EntityModel.of(commentEditResponseDto);
        WebMvcLinkBuilder self = linkTo(methodOn(this.getClass()).editComment(commentEditRequestDto, boardId, commentId, request));

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
    @DeleteMapping("/{boardId}/comments/{commentId}")
    public ResponseEntity deleteComment(@ApiParam(value = "게시글 PK", required = true) @PathVariable Long boardId,
                                        @ApiParam(value = "댓글 PK", required = true) @PathVariable Long commentId,
                                        HttpServletRequest request) {
        //게시글이 존재하는지 검사
        Board board = boardService.retrieveOne(boardId);

        //댓글이 존재하는 확인
        Comment comment = commentService.retrieveOne(commentId);

        String token = jwtTokenProvider.resolveToken(request);
        Member member = jwtTokenProvider.retrieveMember(token);

        if (comment.getMember().getId() != member.getId()) {
            throw new NotOwnBoardException("게시글의 권한이 없습니다.");
        }

        //삭제
        commentService.deleteComment(board, commentId);

        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "게시글의 댓글 좋아요", notes = "게시글의 댓글을 좋아합니다..")
    @ApiResponses({
            @ApiResponse(code = 204, message = "댓글 좋아요를 정상적으로 수행했습니다."),
            @ApiResponse(code = 400, message = "존재하지 않는 게시글 입니다."),
            @ApiResponse(code = 401, message = "토큰 검증 실패(인증 실패)"),
    })
    @PutMapping("/{boardId}/comments/{commentId}/likes")
    public ResponseEntity updateCommentLike(@ApiParam(value = "게시글 PK", required = true) @PathVariable Long boardId,
                                            @ApiParam(value = "댓글 PK", required = true) @PathVariable Long commentId
    ,HttpServletRequest request) {
        //게시글이 존재하는지 검사
        Board board = boardService.retrieveOne(boardId);

        String token = jwtTokenProvider.resolveToken(request);
        Member member = jwtTokenProvider.retrieveMember(token);

        if (member.getLikeId().contains(commentId)) {
            throw new DuplicatedLikeException("이미 좋아요를 눌렀습니다.");
        }


        //댓글이 존재하는지 같이 검사한다.
        commentService.updateCommentLike(member, commentId);

        return ResponseEntity.noContent().build();
    }


    
    //댓글 채택: 채택 수정, 채택 취소 안됨
    @ApiOperation(value = "게시글의 댓글 채택", notes = "게시글의 댓글을 채택합니다.")
    @ApiResponses({
            @ApiResponse(code = 204, message = "댓글 채택 정상적으로 수행했습니다."),
            @ApiResponse(code = 400, message = "존재하지 않는 댓글 입니다."),
            @ApiResponse(code = 401, message = "토큰 검증 실패(인증 실패)"),
    })
    @PutMapping("/{boardId}/comments/{commentId}/selections")
    public ResponseEntity selectComment(
            @ApiParam(value = "게시글 PK", required = true) @PathVariable Long boardId,
            @ApiParam(value = "댓글 PK", required = true) @PathVariable Long commentId, HttpServletRequest request) {

        commentService.selectComment(boardId, commentId, request);

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
