package com.delivery.domain.auth.service;

import com.delivery.domain.auth.dto.SignUpRequestDto;
import com.delivery.domain.user.entity.User;
import com.delivery.domain.user.repository.UserRepository;
import com.delivery.testconfig.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static com.delivery.domain.user.entity.Role.CUSTOMER;
import static com.delivery.domain.user.entity.Role.ROLE_CUSTOMER;
import static org.assertj.core.api.Assertions.assertThat;

// TODO : 동시성 문제 확인 후 예외 처리 확인해봐야함
class AuthServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired private AuthService authService;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    // TODO : JWT 구현 완료 후 테스트 코드 수정
    @Nested
    @DisplayName("회원가입 테스트")
    class SignUp {
        @Test
        @Transactional
        @DisplayName("회원가입 성공")
        void signUp_success() {
            // given
            SignUpRequestDto request =
                    new SignUpRequestDto(
                            "test1234", "testtest1234!", "test", "01012345678", CUSTOMER);

            // when
            var response = authService.signUp(request);

            // then
            User savedUser = userRepository.findByUsername(request.getUsername()).orElseThrow();

            assertThat(savedUser.getId()).isEqualTo(response.getId());
            assertThat(savedUser.getUsername()).isEqualTo(response.getUsername());
            assertThat(savedUser.getNickName()).isEqualTo(response.getNickname());

            //            assertTrue(response.success());
            //            assertThat(response.code()).isEqualTo(HttpStatus.OK.value());
            //            assertThat(response.message()).isEqualTo("회원가입 성공");
            //            assertThat(response.data().getId()).isEqualTo(savedUser.getId());
            //
            // assertThat(response.data().getUsername()).isEqualTo(savedUser.getUsername());
            //
            // assertThat(response.data().getNickname()).isEqualTo(savedUser.getNickName());
            //            assertThat(passwordEncoder.matches(request.getPassword(),
            // savedUser.getPassword()))
            //                    .isTrue();
            //            assertThat(response.error()).isNull();
        }
    }
}
