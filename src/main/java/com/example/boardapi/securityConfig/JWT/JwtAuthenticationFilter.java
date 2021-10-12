package com.example.boardapi.securityConfig.JWT;

import com.example.boardapi.exception.TokenErrorCode;
import com.example.boardapi.exception.UserNotFoundException;
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

    //우리가 사용하는 Custom Exception 은 Spring 의 영역이다. 그에 반해 Spring Security 는 Spring 이전에 필터링 한다.
    //그러니까 아무리 Security 단에서 예외가 발생해도 절대 Spring 의 DispatcherServlet 까지 닿을 수가 없다는 말이다.

    // 해당 필터가 UsernamePasswordAuthenticationFilter 보다 먼저 실행된다.

    private final JwtTokenProvider jwtTokenProvider;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //회원 가입, 로그인에는 헤더를 끄자
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);

        if (token != null && jwtTokenProvider.validateToken(token, (HttpServletRequest) request)) {

            //UsernamePasswordAuthenticationToken 의 인증객체이다.
            Authentication authentication;
            try {
                //해당 토큰의 subject 로 조회하였으나, 데이터베이스에 존재하지 않는 경우.
                //(해당 토큰의 payload 안의 claim 안의 subject 의 아이디를 가진 user 가 없는 경우)
                authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (UserNotFoundException e) {
                log.info("token`s user does not exist");
                request.setAttribute("exception", TokenErrorCode.NOT_EXIST_USER_TOKEN);
            }
            //securityContext 에 인증 객체를 넣어줘야 하는 이유 : securityContext 에 인증 객체가 저장 되어야 인증이 성공했다고 판단해서.
        }

        //예외가 발생할 경우 doFilter 를 통해 넘어가 핸들링한다.
        chain.doFilter(request, response);
    }
}
