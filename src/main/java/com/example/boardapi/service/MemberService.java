package com.example.boardapi.service;

import com.example.boardapi.dto.member.request.MemberEditRequestDto;
import com.example.boardapi.dto.member.request.MemberJoinRequestDto;
import com.example.boardapi.dto.member.request.MemberLoginRequestDto;
import com.example.boardapi.dto.member.response.MemberEditResponseDto;
import com.example.boardapi.dto.member.response.MemberJoinResponseDto;
import com.example.boardapi.dto.member.response.MemberLoginResponseDto;
import com.example.boardapi.dto.member.response.MemberRetrieveResponseDto;
import com.example.boardapi.entity.Member;
import com.example.boardapi.exception.DuplicateLoginIdException;
import com.example.boardapi.exception.MemberNotFoundException;
import com.example.boardapi.exception.NotOwnMemberException;
import com.example.boardapi.exception.message.MemberExceptionMessage;
import com.example.boardapi.config.jwt.JwtTokenProvider;
import com.example.boardapi.repository.board.BoardRepository;
import com.example.boardapi.repository.comment.CommentRepository;
import com.example.boardapi.repository.member.MemberRepository;
import com.example.boardapi.repository.scrap.ScrapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service("userDetailsService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService implements UserDetailsService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final BoardRepository boardRepository;
    private final ScrapRepository scrapRepository;
    private final ModelMapper modelMapper;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원 가입
     */
    @Transactional
    public MemberJoinResponseDto join(MemberJoinRequestDto memberJoinRequestDto) {

        findDuplicatedLogin(memberJoinRequestDto.getLoginId());

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

        Member saveMember = memberRepository.save(member);

        MemberJoinResponseDto memberJoinResponseDto = modelMapper.map(saveMember, MemberJoinResponseDto.class);

        return memberJoinResponseDto;
    }

    /**
     *  단건 조회
     */
    public Member retrieveOne(Long memberId) {
        Member findMember = memberRepository.findById(memberId).orElse(null);

        if (findMember == null) {
            throw new MemberNotFoundException(MemberExceptionMessage.MEMBER_NOT_FOUND);
        }

        return findMember;
    }

    public MemberRetrieveResponseDto retrieveOneWithDto(Long memberId) {
        Member findMember = retrieveOne(memberId);

        MemberRetrieveResponseDto memberRetrieveResponseDto = modelMapper.map(findMember, MemberRetrieveResponseDto.class);

        return memberRetrieveResponseDto;
    }

    /**
     * 전체 조회
     */
    public List<Member> retrieveAll() {
        return memberRepository.findAll();
    }

    public List<MemberRetrieveResponseDto> retrieveAllWithDto() {
        List<Member> members = retrieveAll();

        List<MemberRetrieveResponseDto> memberRetrieveResponseDtos = members.stream().map(
                m -> modelMapper.map(m, MemberRetrieveResponseDto.class))
                .collect(Collectors.toList());

        return memberRetrieveResponseDtos;
    }

    /**
     * 회원 정보 수정
     */
    @Transactional
    public MemberEditResponseDto editMember(Long memberId, MemberEditRequestDto memberEditRequestDto, String token) {

        //토큰의 member
        Member member = jwtTokenProvider.getMember(token);

        //아이디의 member
        Member findMember = retrieveOne(memberId);

        if (findMember.getId() != member.getId()) {
            throw new NotOwnMemberException(MemberExceptionMessage.NOT_OWN_MEMBER);
        }

        //수정한 비밀번호를 인코딩
        String password = memberEditRequestDto.getPassword();
        String encode = passwordEncoder.encode(password);
        //인코딩 후 다시 설정
        memberEditRequestDto.setPassword(encode);

        //
        findMember.changeMemberInfo(memberEditRequestDto);

        MemberEditResponseDto mappedResponseDto = modelMapper.map(findMember, MemberEditResponseDto.class);

        return mappedResponseDto;
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void deleteMember(Long memberId) {
        commentRepository.deleteAllByMemberId(memberId);
        scrapRepository.deleteByMemberId(memberId);
        boardRepository.deleteAllByMemberId(memberId);
        memberRepository.deleteById(memberId);
    }

    @Transactional
    public void deleteMember(Long memberId, String token) {

        Member member = jwtTokenProvider.getMember(token);

        Member findMember = retrieveOne(memberId);

        if (findMember.getId() != member.getId()) {
            throw new NotOwnMemberException(MemberExceptionMessage.NOT_OWN_MEMBER);
        }
        commentRepository.deleteAllByMemberId(memberId);
        scrapRepository.deleteByMemberId(memberId);
        boardRepository.deleteAllByMemberId(memberId);
        memberRepository.deleteById(memberId);

    }

    /**
     * 중복 아이디 검사
     */
    public void findDuplicatedLogin(String loginId) {
        Member findMember = memberRepository.findByLoginId(loginId).orElse(null);

        //비어있을 경우
        if (findMember != null) {
            throw new DuplicateLoginIdException(MemberExceptionMessage.DUPLICATE_LOGIN_ID);
        }
    }

    /**
     * 스프링 시큐리티에서 활용됨
     * 아이디 조회
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByLoginId(username)
                .orElseThrow(
                        () -> {throw new MemberNotFoundException(MemberExceptionMessage.MEMBER_NOT_FOUND);}
                );
        return member;
    }

    /**
     * 로그인
     */
    public MemberLoginResponseDto login(MemberLoginRequestDto memberLoginRequestDto) {
        //아이디가 있는지 검증을 한다.
        Member member = memberRepository.findByLoginId(memberLoginRequestDto.getLoginId()).orElseThrow(
                () -> new MemberNotFoundException()
        );

        if (!passwordEncoder.matches(memberLoginRequestDto.getPassword(), member.getPassword())) {
            throw new BadCredentialsException(MemberExceptionMessage.INVALID_PASSWORD);
        }

        String token = jwtTokenProvider.createToken(member.getLoginId(), member.getRoles());
        MemberLoginResponseDto memberLoginResponseDto = new MemberLoginResponseDto(token, member.getId());

        return memberLoginResponseDto;
    }
}
