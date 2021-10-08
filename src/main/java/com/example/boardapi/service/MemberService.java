package com.example.boardapi.service;

import com.example.boardapi.domain.Member;
import com.example.boardapi.dto.MemberEditDto;
import com.example.boardapi.exception.DuplicateLoginIdException;
import com.example.boardapi.exception.UserNotFoundException;
import com.example.boardapi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("userDetailsService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService implements UserDetailsService{

    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     */
    @Transactional
    public Member join(Member member) {
        findDuplicatedLogin(member.getLoginId());
        Member saveMember = memberRepository.save(member);
        return saveMember;
    }

    /**
     *  단건 조회
     */
    public Member retrieveOne(Long memberId) {
        Member findMember = memberRepository.findById(memberId).orElse(null);

        if (findMember == null) {
            throw new UserNotFoundException("해당 유저는 존재하지 않습니다.");
        }

        return findMember;
    }

    /**
     * 전체 조회
     */
    public List<Member> retrieveAll() {
        return memberRepository.findAll();
    }

    /**
     * 회원 정보 수정
     */
    @Transactional
    public void editMember(Long id, MemberEditDto editMemberDto) {
        Member findMember = retrieveOne(id);
        findMember.changeMemberInfo(editMemberDto);
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void deleteMember(Long id) {
        try {
            memberRepository.deleteById(id);
        } catch (Exception e) {
            throw new UserNotFoundException("해당 유저는 존재하지 않습니다.");
        }
    }

    /**
     * 중복 아이디 검사
     */
    public void findDuplicatedLogin(String loginId) {
        Member findMember = memberRepository.findByLoginId(loginId).orElse(null);

        //비어있을 경우
        if (findMember != null) {
            throw new DuplicateLoginIdException("중복된 아이디가 존재합니다.");
        }
    }
    
    
    //스프링 시큐리티에서 활용됨
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByLoginId(username)
                .orElseThrow(
                        () -> new UserNotFoundException("해당 유저가 없습니다.")
                );
        return member;
    }
}