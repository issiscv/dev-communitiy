package com.example.boardapi.securityConfig.JWT;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor                    //필터를 생성할 수 있게 도와줌 ㅋ
@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {

    // 해당 필터가 UsernamePasswordAuthenticationFilter 보다 먼저 실행된다.

    private final JwtTokenProvider jwtTokenProvider;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            log.info("filter is worked");
            //UsernamePasswordAuthenticationToken 의 인증객체이다.
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            //securityContext 에 인증 객체를 넣어줘야 하는 이유 : securityContext 에 인증 객체가 저장 되어야 인증이 성공했다고 판단해서.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}
