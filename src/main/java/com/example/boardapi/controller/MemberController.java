package com.example.boardapi.controller;

import com.example.boardapi.securityConfig.JWT.JwtTokenProvider;
import com.example.boardapi.domain.Member;
import com.example.boardapi.dto.*;
import com.example.boardapi.exception.UserNotFoundException;
import com.example.boardapi.repository.MemberRepository;
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

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Api("MemberController")
@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    //회원 가입 api
    @ApiOperation(value = "회원가입", notes = "회원 객체 DTO 를 통해 회원가입을 진행합니다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "회원 가입 성공"),
            @ApiResponse(code = 400, message = "중복된 아이디 입니다.")
    })
    @PostMapping("/members")
    public ResponseEntity createMember(@ApiParam(value = "회원 객체 DTO", required = true) @RequestBody MemberRequestDto memberDto) {

        Member member = Member.builder()
                .loginId(memberDto.getLoginId())
                .password(memberDto.getPassword())
                .name(memberDto.getName())
                .age(memberDto.getAge())
                .city(memberDto.getCity())
                .street(memberDto.getStreet())
                .zipcode(memberDto.getZipcode())
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .roles(Collections.singletonList("ROLE_USER"))// 최초 가입시 USER 로 설정,
                                                            // 단 한개의 객체만 저장 가능한 컬렉션을 만들고 싶을 때 사용한다.
                .build();

        //회원 가입 저장
        Member joinMember = memberService.join(member);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(joinMember.getId())
                .toUri();

        MemberResponseDto mappedResponseDto = modelMapper.map(joinMember, MemberResponseDto.class);

        return ResponseEntity.created(uri).body(mappedResponseDto);
    }

    //로그인
    @ApiOperation(value = "로그인", notes = "로그인에 성공 시 JWT 토큰이 발급됩니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "로그인 성공"),
            @ApiResponse(code = 400, message = "로그인 실패")
    })
    @PostMapping("/members/login")
    public String login(@ApiParam(value = "회원 가입 DTO", required = true) @RequestBody MemberLoginDto memberLoginDto) {
        //아이디가 있는지 검증을 한다.
        Member member = memberRepository.findByLoginId(memberLoginDto.getLoginId()).orElseThrow(
                () -> new UserNotFoundException("해당 아이디는 존재하지 않습니다.")
        );

        if (!passwordEncoder.matches(memberLoginDto.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("비밀번호가 틀렸습니다.");
        }

        return jwtTokenProvider.createToken(member.getLoginId(), member.getRoles());
    }

    //단건 조회 api
    @ApiOperation(value = "회원 단건 조회", notes = "회원의 PK를 경로 변수에 넣어 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "회원 단건 조회 성공"),
            @ApiResponse(code = 403, message = "로그인이 필요합니다."),
            @ApiResponse(code = 400, message = "존재하지 않는 회원입니다.")
    })
    @GetMapping("/members/{id}")
    public ResponseEntity retrieveMember(@ApiParam(value = "회원 PK", required = true) @PathVariable Long id) {
        Member member = memberService.retrieveOne(id);

        MemberResponseDto mappedResponseDto = modelMapper.map(member, MemberResponseDto.class);

        return ResponseEntity.ok(mappedResponseDto);
    }

    //전체 조회 api
    @ApiOperation(value = "회원 전체 조회", notes = "회원의 전체를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "회원 단건 조회 성공"),
            @ApiResponse(code = 403, message = "로그인이 필요합니다."),
            @ApiResponse(code = 400, message = "존재하지 않는 회원입니다.")
    })
    @GetMapping("/members")
    public ResponseEntity retrieveAllMember() {

        List<Member> members = memberService.retrieveAll();

        List<MemberResponseDto> collect = members.stream().map(
                m -> modelMapper.map(m, MemberResponseDto.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(collect);
    }

    //회원 정보 수정 api
    @ApiOperation(value = "회원 정보 수정", notes = "회원의 정보를 수정합니다. 이름, 도시, 거리, 번지, 비밀번호를 꼭 널어주세요")
    @ApiResponses({
            @ApiResponse(code = 201, message = "회원 정보가 수정되었습니다."),
            @ApiResponse(code = 403, message = "로그인이 필요합니다."),
            @ApiResponse(code = 400, message = "존재하지 않는 회원입니다.")
    })
    @PutMapping("/members/{id}")
    public ResponseEntity editMember(@ApiParam(value = "회원 수정 DTO", required = true) @RequestBody MemberEditDto editMemberDto, @PathVariable Long id) {

        Member findMember = memberService.retrieveOne(id);

        //수정
        memberService.editMember(id, editMemberDto);

        MemberResponseDto mappedResponseDto = modelMapper.map(findMember, MemberResponseDto.class);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .build().toUri();

        return ResponseEntity.created(uri).body(mappedResponseDto);
    }
    
    //회원 탈퇴 api
    @ApiOperation(value = "회원 탈퇴", notes = "회원 탈퇴를 위해 회원의 PK를 경로 변수에 넣어주세요")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 회원 탈퇴가 되었습니다."),
            @ApiResponse(code = 403, message = "로그인이 필요합니다."),
            @ApiResponse(code = 400, message = "존재하지 않는 회원입니다.")
    })
    @DeleteMapping("/members/{id}")
    public ResponseEntity deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.ok("성공적으로 회원 탈퇴가 되었습니다.");
    }
}
