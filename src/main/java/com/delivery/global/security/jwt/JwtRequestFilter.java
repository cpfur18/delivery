package com.delivery.global.security.jwt;

import com.delivery.common.RestApiResponse;
import com.delivery.domain.user.exception.AuthErrorCode;
import com.delivery.domain.user.exception.UserErrorCode;
import com.delivery.domain.user.exception.UserException;
import com.delivery.global.cache.BlackListRepository;
import com.delivery.global.cache.UserCacheRepository;
import com.delivery.global.exception.ErrorCode;
import com.delivery.global.security.config.CustomUserDetails;
import com.delivery.global.security.config.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private final CustomUserDetailsService customUserDetailsService;
    private final ObjectMapper objectMapper;
    private final BlackListRepository blackListRepository;
    private final UserCacheRepository userCacheRepository;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String accessToken = jwtUtil.resolveAccessToken(request);
        ErrorCode errorCode = null;

        if (accessToken != null) {
            try {
                UUID sessionId = jwtUtil.getSessionIdFromAccessToken(accessToken);

                // 블랙 리스트 등록 여부 확인
                if (blackListRepository.findByKey(sessionId) != null) {
                    errorCode = AuthErrorCode.BLACKLISTED_TOKEN;
                    setErrorResponse(response, errorCode);
                    log.info(AuthErrorCode.BLACKLISTED_TOKEN.getMessage());
                    return;
                }
                UUID userUuid = jwtUtil.getUserUuidFromAccessToken(accessToken);

                // 중복 인증 방지
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    CustomUserDetails userDetails = userCacheRepository.findByKey(userUuid);

                    try {
                        if (userDetails == null) {
                            userDetails = customUserDetailsService.loadUserByUuid(userUuid);
                            userCacheRepository.save(userUuid, userDetails);
                            log.info("Jwt 캐싱 {} : {}", userUuid, userDetails);
                        }
                    } catch (UserException e) {
                        errorCode = UserErrorCode.NOT_EXIST_USER;
                        setErrorResponse(response, errorCode);
                        log.warn("존재하지 않는 회원입니다(UUID) : {}", userUuid, e);
                        return;
                    }

                    // 토큰 검증
                    if (jwtUtil.validateToken(accessToken, userDetails)) {
                        setAuthentication(request, userDetails);
                    } else {
                        errorCode = AuthErrorCode.INVALID_ACCESS_TOKEN;
                        setErrorResponse(response, errorCode);
                        log.warn(errorCode.getMessage());
                        return;
                    }
                }
            } catch (IllegalArgumentException e) {
                errorCode = AuthErrorCode.INVALID_ACCESS_TOKEN;
                setErrorResponse(response, errorCode);
                log.warn(errorCode.getMessage(), e);
                return;
            } catch (ExpiredJwtException e) {
                errorCode = AuthErrorCode.EXPIRED_ACCESS_TOKEN;
                setErrorResponse(response, errorCode);
                log.warn(errorCode.getMessage(), e);
                return;
            }
        } else {
            logger.debug("Authorization Header가 없습니다.");
        }
        filterChain.doFilter(request, response);
    }

    // 컨택스트 생성
    private void setAuthentication(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        usernamePasswordAuthenticationToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    // Response 규격 통일, 따로 분리 가능성 있음
    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode)
            throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        var errorResponse =
                RestApiResponse.fail(
                        errorCode.getHttpStatus(), errorCode.getMessage(), errorCode.getName());

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
