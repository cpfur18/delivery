package com.delivery.global.security.jwt;

import com.delivery.common.RestApiResponse;
import com.delivery.global.exception.GlobalErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        String contentType = "application/json;charset=UTF-8";
        response.setStatus(GlobalErrorCode.FORBIDDEN.getHttpStatus().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(contentType);
        response.setHeader(HttpHeaders.CONTENT_TYPE, contentType);

        response.getWriter()
                .write(
                        objectMapper.writeValueAsString(
                                RestApiResponse.fail(
                                        GlobalErrorCode.FORBIDDEN.getHttpStatus(),
                                        GlobalErrorCode.FORBIDDEN.getMessage(),
                                        GlobalErrorCode.FORBIDDEN.getName())));
    }
}
