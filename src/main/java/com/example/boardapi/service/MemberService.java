package com.example.boardapi.service;

import com.example.boardapi.entity.Member;
import com.example.boardapi.dto.member.request.MemberEditRequestDto;
import com.example.boardapi.exception.DuplicateLoginIdException;
import com.example.boardapi.exception.MemberNotFoundException;
import com.example.boardapi.exception.message.MemberExceptionMessage;
import com.example.boardapi.repository.board.BoardRepository;
import com.example.boardapi.repository.comment.CommentRepository;
import com.example.boardapi.repository.member.MemberRepository;
import com.example.boardapi.repository.scrap.ScrapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("userDetailsService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService implements UserDetailsService{

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final BoardRepository boardRepository;
    private final ScrapRepository scrapRepository;

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
            throw new MemberNotFoundException(MemberExceptionMessage.MEMBER_NOT_FOUND);
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
    public void editMember(Long id, MemberEditRequestDto editMemberDto) {
        Member findMember = retrieveOne(id);

        //수정한 비밀번호를 인코딩
        String password = editMemberDto.getPassword();
        String encode = passwordEncoder.encode(password);
        //인코딩 후 다시 설정
        editMemberDto.setPassword(encode);

        findMember.changeMemberInfo(editMemberDto);
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void deleteMember(Long memberId) {
        try {
            commentRepository.deleteAllByMemberId(memberId);
            scrapRepository.deleteByMemberId(memberId);
            boardRepository.deleteAllByMemberId(memberId);
            memberRepository.deleteById(memberId);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
