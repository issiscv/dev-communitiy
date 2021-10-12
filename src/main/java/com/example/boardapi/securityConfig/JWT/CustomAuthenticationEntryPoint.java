package com.example.boardapi.securityConfig.JWT;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String message = (String)request.getAttribute("exception");
        String details = authException.getMessage();

        //X-AUTH-TOKEN 헤더를 넣지 않으면 null
        if (message == null) {
            setResponse(response, "request header must contain X-AUTH-TOKEN header", details);
            return;
        } else {
            setResponse(response, message, details);
        }
    }
    public void setResponse(HttpServletResponse response, String message, String details) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println(
                        "{ \"timestamp\" : \"" + new Date() + "\"," +
                        " \"message\" : \"" +  message + "\"," +
                        " \"details\" : " + details + "\"");
    }

    public abstract static class TokenErrorCode {

        public static final String NOT_EXIST_USER_TOKEN = "Token`s subject user does not exist, May be that user is deleted";
        public static final String EXPIRED_TOKEN = "X-AUTH-HEADER in the request header is expired. Issue new Token";
        public static final String MALFORMED_TOKEN = "X-AUTH-HEADER in the request header is malformed, Write proper token value";
        public static final String EMPTY_TOKEN = "X-AUTH-HEADER in the request header is empty. Fill the X-AUTH-HEADER`s value";
        public static final String SIGNATURE_NOT_MATCH_TOKEN = "JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.";
    }
}
