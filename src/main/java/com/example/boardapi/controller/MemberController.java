package com.example.boardapi.controller;

import com.example.boardapi.dto.board.response.BoardRetrieveAllPagingResponseDto;
import com.example.boardapi.dto.member.request.MemberEditRequestDto;
import com.example.boardapi.dto.member.request.MemberJoinRequestDto;
import com.example.boardapi.dto.member.request.MemberLoginRequestDto;
import com.example.boardapi.dto.member.response.MemberEditResponseDto;
import com.example.boardapi.dto.member.response.MemberJoinResponseDto;
import com.example.boardapi.dto.member.response.MemberLoginResponseDto;
import com.example.boardapi.dto.member.response.MemberRetrieveResponseDto;
import com.example.boardapi.entity.Member;
import com.example.boardapi.jwt.JwtTokenProvider;
import com.example.boardapi.service.BoardService;
import com.example.boardapi.service.MemberService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final BoardService boardService;
    private final JwtTokenProvider jwtTokenProvider;

    //회원 가입 api
    @ApiOperation(value = "회원가입", notes = "MemberJoinRequestDto DTO 를 통해 회원가입을 진행합니다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "회원 가입 성공"),
            @ApiResponse(code = 400, message = "중복된 아이디 or 잘못된 요청 or 검증 실패")
    })
    @PostMapping("/members")
    public ResponseEntity<EntityModel<MemberJoinResponseDto>> createMember(@ApiParam(value = "회원 객체 DTO", required = true) @RequestBody @Valid
                                                                      MemberJoinRequestDto memberJoinRequestDto) {

        //회원 가입 저장
        MemberJoinResponseDto memberJoinResponseDto = memberService.join(memberJoinRequestDto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(memberJoinResponseDto.getId())
                .toUri();

        //ip
        String ip = getIp();

        //hateoas 기능 추가
        EntityModel<MemberJoinResponseDto> model = EntityModel.of(memberJoinResponseDto);
        WebMvcLinkBuilder self = linkTo(methodOn(this.getClass()).createMember(memberJoinRequestDto));
        WebMvcLinkBuilder login = linkTo(methodOn(this.getClass()).login(new MemberLoginRequestDto()));
        //self
        model.add(self.withSelfRel());
        model.add(Link.of("http://"+ip+":8080/swagger-ui/#/", "profile"));
        model.add(login.withRel("로그인"));

        return ResponseEntity.created(uri).body(model);
    }

    //로그인
    @ApiOperation(value = "로그인", notes = "로그인에 성공 시 JWT 토큰이 발급됩니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "로그인 성공"),
            @ApiResponse(code = 400, message = "로그인 실패 or 잘못된 요청 or 검증 실패")
    })
    @PostMapping("/members/login")
    public ResponseEntity<EntityModel<MemberLoginResponseDto>> login(@ApiParam(value = "회원 가입 DTO", required = true) @RequestBody @Valid
                                MemberLoginRequestDto memberLoginRequestDto) {

        MemberLoginResponseDto memberLoginResponseDto = memberService.login(memberLoginRequestDto);

        //ip
        String ip = getIp();

        //hateoas 기능 추가
        EntityModel<MemberLoginResponseDto> model = EntityModel.of(memberLoginResponseDto);
        WebMvcLinkBuilder self = linkTo(methodOn(this.getClass()).login(memberLoginRequestDto));
        //self
        model.add(self.withSelfRel());
        model.add(Link.of("http://"+ip+":8080/swagger-ui/#/", "profile"));

        return ResponseEntity.ok().body(model);
    }

    //단건 조회 api
    @ApiOperation(value = "회원 단건 조회", notes = "회원의 PK를 경로 변수에 넣어 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "회원 단건 조회 성공"),
            @ApiResponse(code = 400, message = "존재하지 않는 회원입니다."),
    })
    @GetMapping("/members/{memberId}")
    public ResponseEntity<EntityModel<MemberRetrieveResponseDto>> retrieveMember(@ApiParam(value = "회원 PK", required = true) @PathVariable Long memberId) {

        MemberRetrieveResponseDto memberRetrieveResponseDto = memberService.retrieveOneWithDto(memberId);

        //ip
        String ip = getIp();

        //hateoas 기능 추가
        EntityModel<MemberRetrieveResponseDto> model = EntityModel.of(memberRetrieveResponseDto);
        WebMvcLinkBuilder self = linkTo(methodOn(this.getClass()).retrieveMember(memberId));
        //self
        model.add(self.withSelfRel());
        model.add(Link.of("http://"+ip+":8080/swagger-ui/#/", "profile"));

        return ResponseEntity.ok(model);
    }

    //전체 조회 api
    @ApiOperation(value = "회원 전체 조회", notes = "회원의 전체를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "회원 전체 조회 성공"),
    })
    @GetMapping("/members")
    public ResponseEntity<List<MemberRetrieveResponseDto>> retrieveAllMember() {

        List<MemberRetrieveResponseDto> memberRetrieveResponseDtos = memberService.retrieveAllWithDto();

        return ResponseEntity.ok(memberRetrieveResponseDtos);
    }

    //회원 정보 수정 api
    @ApiOperation(value = "회원 정보 수정", notes = "회원의 정보를 수정합니다. 이름, 도시, 거리, 번지, 비밀번호를 꼭 널어주세요")
    @ApiResponses({
            @ApiResponse(code = 201, message = "회원 정보가 수정되었습니다."),
            @ApiResponse(code = 400, message = "존재하지 않는 회원입니다. or 검증 실패 or 잘못된 요청"),
            @ApiResponse(code = 401, message = "토큰 검증 실패(인증 실패)")
    })
    @PutMapping("/members/{memberId}")
    public ResponseEntity<EntityModel<MemberEditResponseDto>> editMember(@ApiParam(value = "회원 수정 DTO", required = true) @RequestBody @Valid MemberEditRequestDto memberEditRequestDto,
                                                                         @ApiParam(value = "회원 PK", required = true) @PathVariable Long memberId,
                                                                         HttpServletRequest request) {

        String token = jwtTokenProvider.resolveToken(request);

        //수정
        MemberEditResponseDto memberEditResponseDto = memberService.editMember(memberId, memberEditRequestDto, token);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .build().toUri();

        //ip
        String ip = getIp();

        //hateoas 기능 추가
        EntityModel<MemberEditResponseDto> model = EntityModel.of(memberEditResponseDto);
        WebMvcLinkBuilder self = linkTo(methodOn(this.getClass()).editMember(memberEditRequestDto, memberId, request));
        //self
        model.add(self.withSelfRel());
        model.add(Link.of("http://"+ip+":8080/swagger-ui/#/", "profile"));

        return ResponseEntity.created(uri).body(model);
    }

    //회원 탈퇴 api
    @ApiOperation(value = "회원 탈퇴", notes = "회원 탈퇴를 위해 회원의 PK를 경로 변수에 넣어주세요")
    @ApiResponses({
            @ApiResponse(code = 204, message = "성공적으로 회원 탈퇴가 되었습니다."),
            @ApiResponse(code = 400, message = "존재하지 않는 회원입니다."),
            @ApiResponse(code = 401, message = "토큰 검증 실패(인증 실패)"),
    })
    @DeleteMapping("/members/{memberId}")
    public ResponseEntity deleteMember(@ApiParam(value = "회원 PK", required = true) @PathVariable Long memberId, HttpServletRequest request) {

        String token = jwtTokenProvider.resolveToken(request);
        memberService.deleteMember(memberId, token);

        return ResponseEntity.noContent().build();
    }

    //특정 사용자가 작성한 게시글 조회
    @ApiOperation(value = "사용자가 작성한 모든 게시글 조회", notes = "회원의 모든 게시글 조회를 위해 회원의 PK를 경로 변수에 넣어주세요")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 회원 사용자의 게시글을 조회하였습니다.."),
            @ApiResponse(code = 400, message = "존재하지 않는 회원입니다. or 잘못된 요청 or 검증 실패"),
    })
    @GetMapping("/members/{memberId}/boards")
    public ResponseEntity<EntityModel<BoardRetrieveAllPagingResponseDto>> retrieveAllOwnBoard (
            @ApiParam(value = "회원의 PK", required = true) @PathVariable Long memberId,
            @ApiParam(value = "페이지 번호", required = false) @RequestParam(defaultValue = "1") int page) {

        //해당 사용자가 존재하는지 검사
        Member findMember = memberService.retrieveOne(memberId);

        //해당 페이지의 요청 결과
        BoardRetrieveAllPagingResponseDto boardRetrieveAllPagingResponseDto = boardService.retrieveAllOwnBoardWithPaging(page, memberId);
        int totalPages = boardRetrieveAllPagingResponseDto.getTotalPages();

        //ip
        String ip = getIp();

        EntityModel<BoardRetrieveAllPagingResponseDto> model = EntityModel.of(boardRetrieveAllPagingResponseDto);

        //hateoas 기능 추가
        WebMvcLinkBuilder self = linkTo(methodOn(this.getClass()).retrieveAllOwnBoard(memberId, page));
        //self
        model.add(self.withSelfRel());
        model.add(Link.of("http://"+ip+":8080/swagger-ui/#/", "profile"));

        if (page > 1) {
            WebMvcLinkBuilder prev = linkTo(methodOn(this.getClass()).retrieveAllOwnBoard(memberId, page-1));
            model.add(prev.withRel("이전"));
        }
        if (page < totalPages) {
            WebMvcLinkBuilder next = linkTo(methodOn(this.getClass()).retrieveAllOwnBoard(memberId, page+1));
            model.add(next.withRel("다음"));
        }

        return ResponseEntity.ok().body(model);
    }


    //특정 사용자가 작성한 댓글의 게시글
    @ApiOperation(value = "사용자가 작성한 댓글의 게시글", notes = "회원의 모든 댓글 조회를 위해 회원의 PK를 경로 변수에 넣어주세요")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 회원 사용자의 댓글 조회하였습니다.."),
            @ApiResponse(code = 400, message = "존재하지 않는 회원입니다 or 잘못된 요청 or 검증 실패"),
    })
    @GetMapping("/members/{memberId}/comments")
    public ResponseEntity<EntityModel<BoardRetrieveAllPagingResponseDto>> retrieveAllOwnComment(@ApiParam(value = "회원의 PK", required = true) @PathVariable Long memberId,
                                                                                                @ApiParam(value = "페이지 번호", required = false) @RequestParam(defaultValue = "1") int page) {
        //해당 사용자가 존재하는지 검사
        Member findMember = memberService.retrieveOne(memberId);

        //회원의 댓글
        BoardRetrieveAllPagingResponseDto boardRetrieveAllPagingResponseDto = boardService.retrieveAllBoardByOwnCommentWithPaging(page, memberId);
        int totalPages = boardRetrieveAllPagingResponseDto.getTotalPages();

        //ip
        String ip = getIp();

        //hateoas 기능 추가
        EntityModel<BoardRetrieveAllPagingResponseDto> model = EntityModel.of(boardRetrieveAllPagingResponseDto);
        WebMvcLinkBuilder self = linkTo(methodOn(this.getClass()).retrieveAllOwnComment(memberId, page));
        //self
        model.add(self.withSelfRel());
        model.add(Link.of("http://"+ip+":8080/swagger-ui/#/", "profile"));

        if (page > 1) {
            WebMvcLinkBuilder prev = linkTo(methodOn(this.getClass()).retrieveAllOwnComment(memberId, page-1));
            model.add(prev.withRel("이전"));
        }
        if (page < totalPages) {
            WebMvcLinkBuilder next = linkTo(methodOn(this.getClass()).retrieveAllOwnComment(memberId, page+1));
            model.add(next.withRel("다음"));
        }

        return ResponseEntity.ok().body(model);
    }

     //사용자의 스크랩 목록
    @ApiOperation(value = "사용자의 스크랩 목록", notes = "사용자의 스크랩을 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 400, message = "존재하지 않는 회원 입니다."),
            @ApiResponse(code = 401, message = "토큰 검증 실패(인증 실패)")
    })
    @GetMapping("/members/{memberId}/scraps")
    public ResponseEntity<EntityModel<BoardRetrieveAllPagingResponseDto>> retrieveAllScrapBoards(@ApiParam(value = "회원 PK", required = true) @PathVariable Long memberId,
                                                                                                 @ApiParam(value = "페이지 번호", required = false) @RequestParam(defaultValue = "1") int page) {
        
        //해당 회원이 존재하는지 검사
        Member member = memberService.retrieveOne(memberId);

        BoardRetrieveAllPagingResponseDto boardRetrieveAllPagingResponseDto =
                boardService.retrieveAllBoardByMemberScrapWithPaging(page, memberId);

        //ip
        String ip = getIp();

        //hateoas 기능 추가
        EntityModel<BoardRetrieveAllPagingResponseDto> model = EntityModel.of(boardRetrieveAllPagingResponseDto);
        WebMvcLinkBuilder self = linkTo(methodOn(this.getClass()).retrieveAllScrapBoards(memberId, page));

        //self
        model.add(self.withSelfRel());
        model.add(Link.of("http://"+ip+":8080/swagger-ui/#/", "profile"));

        //페이징 hateoas 를 위한 로직이다.
        if (page > 1) {
            WebMvcLinkBuilder prev = linkTo(methodOn(this.getClass()).retrieveAllScrapBoards(memberId, page-1));
            model.add(prev.withRel("이전"));
        }
        if (page < boardRetrieveAllPagingResponseDto.getTotalPages()) {
            WebMvcLinkBuilder next = linkTo(methodOn(this.getClass()).retrieveAllScrapBoards(memberId, page+1));
            model.add(next.withRel("다음"));
        }

        return ResponseEntity.ok(model);
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
