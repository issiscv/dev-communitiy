package com.example.boardapi.jpa;

import com.example.boardapi.entity.Member;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

@Configuration
public class AwareAudit implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        /**
         * SecurityContext 에서 인증정보를 가져와 주입시킨다.
         * 현재 코드는 현재 Context 유저가 ROLE_USER 인가 권한이 있으면, 해당 Principal name 을 대입하고, 아니면 Null 을 set 한다.
         */

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) authentication.getAuthorities();

        if (authorities.contains(new SimpleGrantedAuthority(("ROLE_USER")))) {
            Member member = (Member) authentication.getPrincipal();
            return Optional.of(member.getName());
        } else {
            return null;
        }
    }
}