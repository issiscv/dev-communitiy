package com.example.boardapi.jpa;

import com.example.boardapi.domain.Member;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Configuration
public class AccountAwareAudit implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        /**
         * SecurityContext 에서 인증정보를 가져와 주입시킨다.
         * 현재 코드는 현재 Context 유저가 USER 인가 권한이 있으면, 해당 Principal name 을 대입하고, 아니면 Null 을 set 한다.
         */
        Optional<String> user = Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(authentication -> {
                    List<GrantedAuthority> auth = (List<GrantedAuthority>) authentication.getAuthorities();
                    boolean isUser = auth.contains(new SimpleGrantedAuthority("ROLE_USER"));

                    if (isUser)  {
                        Member principal = (Member)authentication.getPrincipal();
                        return principal.getName();
                    }
                    return null;
                });

        return user;
    }
}