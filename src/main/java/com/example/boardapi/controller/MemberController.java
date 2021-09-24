package com.example.boardapi.controller;

import com.example.boardapi.domain.Member;
import com.example.boardapi.dto.MemberRequestDto;
import com.example.boardapi.dto.MemberResponseDto;
import com.example.boardapi.dto.EditMemberDto;
import com.example.boardapi.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final ModelMapper modelMapper;
    
    //회원 가입 api
    @PostMapping("/members")
    public ResponseEntity createMember(@RequestBody MemberRequestDto memberDto) {

        memberService.findDuplicatedLogin(memberDto.getLoginId());

        //memberDto 를 Member 엔티티로 변환
        Member mappedMember = modelMapper.map(memberDto, Member.class);

        Member joinMember = memberService.join(mappedMember);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(joinMember.getId())
                .toUri();

        MemberResponseDto mappedResponseDto = modelMapper.map(joinMember, MemberResponseDto.class);

        return ResponseEntity.created(uri).body(mappedResponseDto);
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
    public ResponseEntity editMember(@RequestBody EditMemberDto editMemberDto, @PathVariable Long id) {

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
