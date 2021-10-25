package com.example.boardapi.controller;

import com.example.boardapi.domain.Board;
import com.example.boardapi.domain.Comment;
import com.example.boardapi.dto.board.response.BoardCreateResponseDto;
import com.example.boardapi.dto.board.response.BoardRetrieveOneResponseDto;
import com.example.boardapi.dto.board.response.BoardRetrieveResponseDto;
import com.example.boardapi.dto.comment.response.CommentRetrieveResponseDto;
import com.example.boardapi.dto.member.request.MemberEditRequestDto;
import com.example.boardapi.dto.member.request.MemberLoginRequestDto;
import com.example.boardapi.dto.member.request.MemberJoinRequestDto;
import com.example.boardapi.dto.member.response.MemberEditResponseDto;
import com.example.boardapi.dto.member.response.MemberRetrieveResponseDto;
import com.example.boardapi.dto.member.response.MemberJoinResponseDto;
import com.example.boardapi.dto.member.response.MemberLoginResponseDto;
import com.example.boardapi.security.JWT.JwtTokenProvider;
import com.example.boardapi.domain.Member;
import com.example.boardapi.exception.exception.UserNotFoundException;
import com.example.boardapi.repository.MemberRepository;
import com.example.boardapi.service.BoardService;
import com.example.boardapi.service.CommentService;
import com.example.boardapi.service.MemberService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final BoardService boardService;
    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CommentService commentService;

    //회원 가입 api
    @ApiOperation(value = "회원가입", notes = "MemberJoinRequestDto DTO 를 통해 회원가입을 진행합니다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "회원 가입 성공"),
            @ApiResponse(code = 400, message = "중복된 아이디 or 잘못된 요청 or 검증 실패")
    })
    @PostMapping("/members")
    public ResponseEntity<EntityModel<MemberJoinResponseDto>> createMember(@ApiParam(value = "회원 객체 DTO", required = true) @RequestBody @Valid
                                                                      MemberJoinRequestDto memberJoinRequestDto) {

        Member member = Member.builder()
                .loginId(memberJoinRequestDto.getLoginId())
                .password(memberJoinRequestDto.getPassword())
                .name(memberJoinRequestDto.getName())
                .age(memberJoinRequestDto.getAge())
                .address(memberJoinRequestDto.getAddress())
                .password(passwordEncoder.encode(memberJoinRequestDto.getPassword()))
                .roles(Collections.singletonList("ROLE_USER"))// 최초 가입시 USER 로 설정,
                // 단 한개의 객체만 저장 가능한 컬렉션을 만들고 싶을 때 사용한다.
                .build();

        //회원 가입 저장
        Member joinMember = memberService.join(member);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(joinMember.getId())
                .toUri();

        MemberJoinResponseDto mappedResponseDto = modelMapper.map(joinMember, MemberJoinResponseDto.class);

        //ip
        String ip = "";
        try {
            InetAddress local = InetAddress.getLocalHost();
            ip = local.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //hateoas 기능 추가
        EntityModel<MemberJoinResponseDto> model = EntityModel.of(mappedResponseDto);
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
        //아이디가 있는지 검증을 한다.
        Member member = memberRepository.findByLoginId(memberLoginRequestDto.getLoginId()).orElseThrow(
                () -> new UserNotFoundException("해당 아이디는 존재하지 않습니다.")
        );

        if (!passwordEncoder.matches(memberLoginRequestDto.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("비밀번호가 틀렸습니다.");
        }

        String token = jwtTokenProvider.createToken(member.getLoginId(), member.getRoles());
        MemberLoginResponseDto memberLoginResponseDto = new MemberLoginResponseDto(token, member.getId());

        //ip
        String ip = "";
        try {
            InetAddress local = InetAddress.getLocalHost();
            ip = local.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

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
        Member member = memberService.retrieveOne(memberId);

        MemberRetrieveResponseDto memberRetrieveResponseDto = modelMapper.map(member, MemberRetrieveResponseDto.class);

        //ip
        String ip = "";
        try {
            InetAddress local = InetAddress.getLocalHost();
            ip = local.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

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

        List<Member> members = memberService.retrieveAll();

        List<MemberRetrieveResponseDto> collect = members.stream().map(
                m -> modelMapper.map(m, MemberRetrieveResponseDto.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(collect);
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
                                                                         @ApiParam(value = "회원 PK", required = true) @PathVariable Long memberId) {

        Member findMember = memberService.retrieveOne(memberId);

        //수정
        memberService.editMember(memberId, memberEditRequestDto);

        MemberEditResponseDto mappedResponseDto = modelMapper.map(findMember, MemberEditResponseDto.class);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .build().toUri();

        //ip
        String ip = "";
        try {
            InetAddress local = InetAddress.getLocalHost();
            ip = local.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //hateoas 기능 추가
        EntityModel<MemberEditResponseDto> model = EntityModel.of(mappedResponseDto);
        WebMvcLinkBuilder self = linkTo(methodOn(this.getClass()).editMember(memberEditRequestDto, memberId));
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
    public ResponseEntity deleteMember(@ApiParam(value = "회원 PK", required = true) @PathVariable Long memberId) {
        memberService.deleteMember(memberId);
        return new ResponseEntity("성공적으로 회원 탈퇴가 되었습니다.", HttpStatus.NO_CONTENT);
    }

    //특정 사용자가 작성한 게시글 조회
    @ApiOperation(value = "사용자가 작성한 모든 게시글 조회", notes = "회원의 모든 게시글 조회를 위해 회원의 PK를 경로 변수에 넣어주세요")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 회원 사용자의 게시글을 조회하였습니다.."),
            @ApiResponse(code = 400, message = "존재하지 않는 회원입니다. or 잘못된 요청 or 검증 실패"),
    })
    @GetMapping("/members/{memberId}/boards")
    public ResponseEntity<CollectionModel<EntityModel<BoardRetrieveOneResponseDto>>> retrieveAllOwnBoard(@ApiParam(value = "회원의 PK", required = true) @PathVariable Long memberId) {

        List<Board> boards = boardService.retrieveAllOwnBoard(memberId);

        List<EntityModel<BoardRetrieveOneResponseDto>> list = new ArrayList<>();

        for (Board board : boards) {
            BoardRetrieveOneResponseDto boardRetrieveOneResponseDto = modelMapper.map(board, BoardRetrieveOneResponseDto.class);
            boardRetrieveOneResponseDto.setAuthor(board.getMember().getName());

            EntityModel<BoardRetrieveOneResponseDto> model = EntityModel.of(boardRetrieveOneResponseDto);
            WebMvcLinkBuilder boardLink = linkTo(methodOn(BoardController.class).retrieveBoard(board.getId()));

            model.add(boardLink.withRel("게시글"));

            list.add(model);
        }

        //ip
        String ip = "";
        try {
            InetAddress local = InetAddress.getLocalHost();
            ip = local.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //hateoas 기능 추가
        CollectionModel<EntityModel<BoardRetrieveOneResponseDto>> model = CollectionModel.of(list);
        WebMvcLinkBuilder self = linkTo(methodOn(this.getClass()).retrieveAllOwnBoard(memberId));
        //self
        model.add(self.withSelfRel());
        model.add(Link.of("http://"+ip+":8080/swagger-ui/#/", "profile"));

        return ResponseEntity.ok().body(model);
    }
    
    //특정 사용자가 작성한 모든 댓글
    @ApiOperation(value = "사용자가 작성한 모든 댓글 조회", notes = "회원의 모든 댓글 조회를 위해 회원의 PK를 경로 변수에 넣어주세요")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 회원 사용자의 댓글 조회하였습니다.."),
            @ApiResponse(code = 400, message = "존재하지 않는 회원입니다 or 잘못된 요청 or 검증 실패"),
    })
    @GetMapping("/members/{memberId}/comments")
    public ResponseEntity<CollectionModel<EntityModel<CommentRetrieveResponseDto>>> retrieveAllOwnComment(@ApiParam(value = "회원의 PK", required = true) @PathVariable Long memberId) {
        
        //해당 유저가 존재하는지 검증을 위해 조회를 해본다.
        memberService.retrieveOne(memberId);

        //회원의 댓글
        List<Comment> comments = commentService.retrieveAllOwnComment(memberId);

        List<EntityModel<CommentRetrieveResponseDto>> list = new ArrayList<>();

        for (Comment comment : comments) {
            CommentRetrieveResponseDto commentRetrieveResponseDto =
                    modelMapper.map(comment, CommentRetrieveResponseDto.class);

            commentRetrieveResponseDto.setAuthor(comment.getMember().getName());
            commentRetrieveResponseDto.setBoardId(comment.getBoard().getId());

            EntityModel<CommentRetrieveResponseDto> model = EntityModel.of(commentRetrieveResponseDto);
            WebMvcLinkBuilder boardLink = linkTo(methodOn(BoardController.class).retrieveBoard(comment.getBoard().getId()));
            model.add(boardLink.withRel("게시글"));

            list.add(model);
        }

        //ip
        String ip = "";
        try {
            InetAddress local = InetAddress.getLocalHost();
            ip = local.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //hateoas 기능 추가
        CollectionModel<EntityModel<CommentRetrieveResponseDto>> model = CollectionModel.of(list);
        WebMvcLinkBuilder self = linkTo(methodOn(this.getClass()).retrieveAllOwnComment(memberId));
        //self
        model.add(self.withSelfRel());
        model.add(Link.of("http://"+ip+":8080/swagger-ui/#/", "profile"));

        return ResponseEntity.ok().body(model);
    }
}
