package com.delivery.domain.auth.service;

import static com.delivery.domain.user.entity.Role.ROLE_CUSTOMER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.delivery.domain.auth.dto.SignUpRequestDto;
import com.delivery.domain.auth.service.AuthService;
import com.delivery.domain.user.repository.UserRepository;
import com.delivery.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceUnitTest {
    @Mock private UserRepository userRepository;

    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private AuthService authService;

    @Nested
    @DisplayName("회원가입 실패 테스트")
    class SignUp {
        @Test
        @DisplayName("회원가입 실패 - 이미 존재하는 username")
        void signUp_fail_when_username_is_invalid() {
            // given
            SignUpRequestDto request =
                    new SignUpRequestDto(
                            "test1234", "testtest1234!", "test", "01012345678", ROLE_CUSTOMER);

            when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

            // when & then
            assertThatThrownBy(() -> authService.signUp(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이미 사용중인 아이디입니다.");

            verify(userRepository).existsByUsername(request.getUsername());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("회원가입 실패 - 이미 존재하는 nickname")
        void signUp_fail_when_nickname_is_invalid() {
            // given
            SignUpRequestDto request =
                    new SignUpRequestDto(
                            "test1234", "testtest1234!", "test", "01012345678", ROLE_CUSTOMER);

            when(userRepository.existsByNickName(request.getNickName())).thenReturn(true);

            // when & then
            assertThatThrownBy(() -> authService.signUp(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이미 사용중인 닉네임입니다.");

            verify(userRepository).existsByNickName(request.getNickName());
            verify(userRepository, never()).save(any());
        }
    }
}
