package com.example.boardapi.controller;

import com.example.boardapi.JWT.JwtTokenProvider;
import com.example.boardapi.domain.Member;
import com.example.boardapi.dto.*;
import com.example.boardapi.exception.UserNotFoundException;
import com.example.boardapi.repository.MemberRepository;
import com.example.boardapi.service.MemberService;
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
    @PostMapping("/members")
    public ResponseEntity createMember(@RequestBody MemberRequestDto memberDto) {

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

    @PostMapping("/members/login")
    public String login(@RequestBody MemberLoginDto memberLoginDto) {
        //아이디가 있는지 검증을 한다.
        Member member = memberRepository.findByLoginId(memberLoginDto.getLoginId()).orElseThrow(
                () -> new UserNotFoundException()
        );

        if (!passwordEncoder.matches(memberLoginDto.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("비밀번호가 틀렸습니다.");
        }

        return jwtTokenProvider.createToken(member.getLoginId(), member.getRoles());
    }

    //단건 조회 api
    @GetMapping("/members/{id}")
    public ResponseEntity retrieveMember(@PathVariable Long id) {
        Member member = memberService.retrieveOne(id);

        MemberResponseDto mappedResponseDto = modelMapper.map(member, MemberResponseDto.class);

        return ResponseEntity.ok(mappedResponseDto);
    }

    //전체 조회 api
    @GetMapping("/members")
    public ResponseEntity retrieveAllMember() {

        List<Member> members = memberService.retrieveAll();

        List<MemberResponseDto> collect = members.stream().map(
                m -> modelMapper.map(m, MemberResponseDto.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(collect);
    }

    //회원 정보 수정 api
    @PutMapping("/members/{id}")
    public ResponseEntity editMember(@RequestBody MemberEditDto editMemberDto, @PathVariable Long id) {

        Member findMember = memberService.retrieveOne(id);

        //수정
        memberService.editMember(id, editMemberDto);

        MemberResponseDto mappedResponseDto = modelMapper.map(findMember, MemberResponseDto.class);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .build().toUri();

        return ResponseEntity.created(uri).body(mappedResponseDto);
    }
    
    //회원 탈퇴 api
    @DeleteMapping("/members/{id}")
    public ResponseEntity deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.ok("성공적으로 회원 탈퇴가 되었습니다.");
    }
}
