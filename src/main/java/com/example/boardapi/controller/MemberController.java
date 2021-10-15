package com.example.boardapi.controller;

import com.example.boardapi.domain.Board;
import com.example.boardapi.domain.Comment;
import com.example.boardapi.dto.board.response.BoardCreateResponseDto;
import com.example.boardapi.dto.board.response.BoardRetrieveResponseDto;
import com.example.boardapi.dto.comment.response.CommentRetrieveResponseDto;
import com.example.boardapi.dto.member.request.MemberEditRequestDto;
import com.example.boardapi.dto.member.request.MemberLoginRequestDto;
import com.example.boardapi.dto.member.request.MemberJoinRequestDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
            @ApiResponse(code = 400, message = "중복된 아이디 입니다."),
            @ApiResponse(code = 403, message = "검증이 실패하였습니다.")
    })
    @PostMapping("/members")
    public ResponseEntity createMember(@ApiParam(value = "회원 객체 DTO", required = true) @RequestBody @Valid
                                               MemberJoinRequestDto memberJoinRequestDto) {

        Member member = Member.builder()
                .loginId(memberJoinRequestDto.getLoginId())
                .password(memberJoinRequestDto.getPassword())
                .name(memberJoinRequestDto.getName())
                .age(memberJoinRequestDto.getAge())
                .city(memberJoinRequestDto.getCity())
                .street(memberJoinRequestDto.getStreet())
                .zipcode(memberJoinRequestDto.getZipcode())
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

        return ResponseEntity.created(uri).body(mappedResponseDto);
    }

    //로그인
    @ApiOperation(value = "로그인", notes = "로그인에 성공 시 JWT 토큰이 발급됩니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "로그인 성공"),
            @ApiResponse(code = 400, message = "로그인 실패"),
            @ApiResponse(code = 403, message = "검증이 실패하였습니다.")
    })
    @PostMapping("/members/login")
    public ResponseEntity login(@ApiParam(value = "회원 가입 DTO", required = true) @RequestBody @Valid
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
        return ResponseEntity.ok().body(memberLoginResponseDto);
    }

    //단건 조회 api
    @ApiOperation(value = "회원 단건 조회", notes = "회원의 PK를 경로 변수에 넣어 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "회원 단건 조회 성공"),
            @ApiResponse(code = 400, message = "존재하지 않는 회원입니다."),
            @ApiResponse(code = 401, message = "토큰 검증 실패")
    })
    @GetMapping("/members/{id}")
    public ResponseEntity retrieveMember(@ApiParam(value = "회원 PK", required = true) @PathVariable Long id) {
        Member member = memberService.retrieveOne(id);

        MemberJoinResponseDto mappedResponseDto = modelMapper.map(member, MemberJoinResponseDto.class);

        return ResponseEntity.ok(mappedResponseDto);
    }

    //전체 조회 api
    @ApiOperation(value = "회원 전체 조회", notes = "회원의 전체를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "회원 전체 조회 성공"),
            @ApiResponse(code = 401, message = "토큰 검증 실패")
    })
    @GetMapping("/members")
    public ResponseEntity retrieveAllMember() {

        List<Member> members = memberService.retrieveAll();

        List<MemberJoinResponseDto> collect = members.stream().map(
                m -> modelMapper.map(m, MemberJoinResponseDto.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(collect);
    }

    //회원 정보 수정 api
    @ApiOperation(value = "회원 정보 수정", notes = "회원의 정보를 수정합니다. 이름, 도시, 거리, 번지, 비밀번호를 꼭 널어주세요")
    @ApiResponses({
            @ApiResponse(code = 201, message = "회원 정보가 수정되었습니다."),
            @ApiResponse(code = 400, message = "존재하지 않는 회원입니다."),
            @ApiResponse(code = 401, message = "토큰 검증 실패"),
            @ApiResponse(code = 403, message = "검증이 실패하였습니다.")
    })
    @PutMapping("/members/{id}")
    public ResponseEntity editMember(@ApiParam(value = "회원 수정 DTO", required = true) @RequestBody @Valid
                                             MemberEditRequestDto memberEditRequestDto, @ApiParam(value = "회원 PK", required = true) @PathVariable Long id) {

        Member findMember = memberService.retrieveOne(id);

        //수정
        memberService.editMember(id, memberEditRequestDto);

        MemberJoinResponseDto mappedResponseDto = modelMapper.map(findMember, MemberJoinResponseDto.class);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .build().toUri();

        return ResponseEntity.created(uri).body(mappedResponseDto);
    }

    //회원 탈퇴 api
    @ApiOperation(value = "회원 탈퇴", notes = "회원 탈퇴를 위해 회원의 PK를 경로 변수에 넣어주세요")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 회원 탈퇴가 되었습니다."),
            @ApiResponse(code = 400, message = "존재하지 않는 회원입니다."),
            @ApiResponse(code = 401, message = "토큰 검증 실패"),
    })
    @DeleteMapping("/members/{id}")
    public ResponseEntity deleteMember(@ApiParam(value = "회원 PK", required = true) @PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.ok("성공적으로 회원 탈퇴가 되었습니다.");
    }

    //특정 사용자가 작성한 게시글 조회
    @GetMapping("/members/{id}/boards")
    public ResponseEntity retrieveAllOwnBoard(@PathVariable Long id) {

        List<Board> boards = boardService.retrieveAllOwnBoard(id);

        List<BoardCreateResponseDto> boardCreateResponseDtoList = boards.stream().map(board -> {
                    BoardCreateResponseDto boardCreateResponseDto = modelMapper.map(board, BoardCreateResponseDto.class);
                    boardCreateResponseDto.setAuthor(board.getMember().getName());
                    return boardCreateResponseDto;
                }
        ).collect(Collectors.toList());

        return ResponseEntity.ok().body(boardCreateResponseDtoList);
    }
    
    //특정 사용자가 작성한 모든 댓글
    @GetMapping("/members/{id}/comments")
    public ResponseEntity retrieveAllOwnComment(@PathVariable Long id) {
        List<Comment> comments = commentService.retrieveAllOwnComment(id);

        List<CommentRetrieveResponseDto> commentRetrieveResponseDtoList = new ArrayList<>();

        for (Comment comment : comments) {
            CommentRetrieveResponseDto commentRetrieveResponseDto =
                    modelMapper.map(comment, CommentRetrieveResponseDto.class);

            commentRetrieveResponseDto.setAuthor(comment.getMember().getName());
            commentRetrieveResponseDto.setBoardId(comment.getBoard().getId());

            commentRetrieveResponseDtoList.add(commentRetrieveResponseDto);
        }

        return ResponseEntity.ok().body(commentRetrieveResponseDtoList);
    }
}
