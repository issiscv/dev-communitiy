package com.example.boardapi.securityConfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String message = (String)request.getAttribute("exception");
        String details = authException.getMessage();

        if (message.equals("token is malformed")) {
            setResponse(response, message, details);
        }

        if (message.equals("token is not null or empty")) {
            setResponse(response, message, details);
        }

        if (message.equals("token is expired")) {
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
}
