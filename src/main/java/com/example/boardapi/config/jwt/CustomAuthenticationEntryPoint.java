package com.example.boardapi.config.jwt;

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
            setResponse(response, "request header must contain X-AUTH-TOKEN header");
            return;
        } else {
            setResponse(response, message);
        }
    }
    public void setResponse(HttpServletResponse response, String message) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println(
                        "{ \"timestamp\" : \"" + new Date() + "\"," +
                        " \"message\" : \"" +  message + "\"");
    }


}
