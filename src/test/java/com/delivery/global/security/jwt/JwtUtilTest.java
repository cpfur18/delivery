package com.delivery.global.security.jwt;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.delivery.global.config.JwtProperties;
import com.delivery.global.security.config.CustomUserDetails;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class JwtUtilTest {
    private JwtUtil jwtUtil;
    private CustomUserDetails userDetails;
    private CustomUserDetails userDetails2;

    @BeforeEach
    void setUp() {
        userDetails =
                CustomUserDetails.builder()
                        .id(1L)
                        .username("dummy")
                        .authorities(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")))
                        .userUuid(UUID.randomUUID())
                        .build();

        userDetails2 =
                CustomUserDetails.builder()
                        .id(2L)
                        .username("dummy2")
                        .authorities(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")))
                        .userUuid(UUID.randomUUID())
                        .build();

        JwtProperties jwtConfig =
                new JwtProperties(
                        "PpxDQ9Sl+fPNcE7xfw1nDT3+AwVSiPsY6qHs0IiU864=",
                        "PpxDQ9Sl+fPNcE7xfw1nDT3+AwVSiPsY6qHs0IiU864=");

        jwtUtil = new JwtUtil(jwtConfig);
    }

    @Test
    @DisplayName("액세스 토큰 생성")
    void createAccessToken() {
        String token =
                jwtUtil.generateAccessToken(userDetails, userDetails.getUserUuid().toString());

        assertThat(token).isNotNull();
    }

    @Test
    @DisplayName("토큰 정보 확인")
    void getUserUsernameFromToken() {
        String token =
                jwtUtil.generateAccessToken(userDetails, userDetails.getUserUuid().toString());

        assertThat(jwtUtil.getUserUsernameFromToken(token)).isEqualTo(userDetails.getUsername());
        assertThat(jwtUtil.getUserUsernameFromToken(token)).isEqualTo(userDetails.getUsername());
    }

    @Test
    @DisplayName("토큰 유효 체크")
    void validateToken() {
        String token =
                jwtUtil.generateAccessToken(userDetails, userDetails.getUserUuid().toString());

        assertThat(jwtUtil.validateToken(token, userDetails)).isTrue();
        assertThat(jwtUtil.validateToken(token, userDetails2)).isFalse();
    }

    @Test
    @DisplayName("JWT 추출")
    void resolveToken_success() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String token =
                jwtUtil.generateAccessToken(userDetails, userDetails.getUserUuid().toString());

        request.addHeader("Authorization", "Bearer " + token);

        var result = jwtUtil.resolveToken(request);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(token);
    }

    @Test
    @DisplayName("JWT 헤더 없을 시 null 반환")
    void resolveToken_fail_when_token_is_null() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        var result = jwtUtil.resolveToken(request);

        assertThat(result).isNull();
    }
}
