package com.example.boardapi.securityConfig;

import com.example.boardapi.securityConfig.JWT.JwtAuthenticationFilter;
import com.example.boardapi.securityConfig.JWT.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .httpBasic().disable() // rest api 만을 고려하여 기본 설정은 해제하겠습니다.
                .csrf().disable() // csrf 보안 토큰 disable처리.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 토큰 기반 인증이므로 세션 역시 사용하지 않습니다.
                .and()
                .authorizeRequests() // 요청에 대한 사용권한 체크
                .antMatchers(HttpMethod.POST,"/members", "/members/login").permitAll()
                .antMatchers( "/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**").permitAll()
                .antMatchers("/**").hasRole("USER")
                .anyRequest().authenticated() // 그외 나머지 요청은 누구나 접근 가능
                .and()
                //permitAll() 의 경로는 필터가 동작하지 않는다.
//        지정된 필터 앞에 커스텀 필터를 추가 (UsernamePasswordAuthenticationFilter 보다 먼저 실행된다)
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), //이 필터를 먼저 타서 토큰을 검사
                        UsernamePasswordAuthenticationFilter.class);            //그리고 이 필터를 타겠다.
        // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 넣는다
    }
}
