package com.delivery.domain.menu.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.delivery.config.AbstractIntegrationTest;
import com.delivery.domain.menu.entity.MenuEntity;
import com.delivery.domain.menu.repository.MenuRepository;
import com.delivery.domain.store.entity.Store;
import com.delivery.domain.store.repository.StoreRepository;
import com.delivery.domain.user.entity.Role;
import com.delivery.domain.user.entity.User;
import com.delivery.domain.user.repository.UserRepository;
import com.delivery.global.cache.RefreshTokenRepository;
import com.delivery.global.security.config.CustomUserDetails;
import com.delivery.global.security.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// MenuControllerTest(@WebMvcTest, addFilters=false)는 필터/메서드 시큐리티가 아예 안 실려서
// "403 테스트"가 사실 목 서비스가 예외를 던진 걸 확인하는 것뿐이었다 - 실제 @PreAuthorize와
// 서비스 레벨 소유권 검증이 진짜로 걸리는지는 이 세션 초반 수동 curl 검증이 유일한 증거였음.
// 이 테스트는 필터를 끄지 않은 진짜 @SpringBootTest로, 실제 JWT + 실제 Store 행을 만들어
// 3단계 권한 모델(역할 → 소유권 → 응답분기) 전체를 실제로 검증한다.
@SpringBootTest(
        properties = {
            "gemini.api-key=test-dummy-key",
            "gemini.base-url=https://generativelanguage.googleapis.com",
            "gemini.model=gemini-1.5-flash"
        })
@AutoConfigureMockMvc
class MenuControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private StoreRepository storeRepository;
    @Autowired private MenuRepository menuRepository;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private RefreshTokenRepository refreshTokenRepository;

    // Auth 도메인의 실제 회원가입/로그인 HTTP 흐름(이 세션 초반 발견한 간헐적 401 이슈 포함)에
    // 의존하지 않도록, User를 직접 저장하고 JwtUtil로 토큰만 발급 - JwtRequestFilter가 검사하는
    // refreshTokenRepository 존재 여부도 직접 채워 넣는다.
    private String issueAccessToken(Set<Role> roles) {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        User user =
                userRepository.save(
                        User.create(
                                "u" + suffix,
                                "encoded-password",
                                "닉" + suffix,
                                "01000000000",
                                roles));
        CustomUserDetails userDetails = CustomUserDetails.from(user);
        UUID sessionId = UUID.randomUUID();
        String accessToken =
                jwtUtil.generateAccessToken(userDetails, user.getUserUuid(), sessionId);
        refreshTokenRepository.save(user.getUserUuid(), "dummy-refresh-token");
        return accessToken;
    }

    private UUID createTestStore(Long ownerId) {
        Store store =
                Store.builder()
                        .userId(ownerId)
                        .categoryId(UUID.randomUUID())
                        .regionId(UUID.randomUUID())
                        .name("통합테스트가게" + UUID.randomUUID())
                        .address("서울시 강남구 테스트로 1")
                        .phone("01012345678")
                        .minOrderAmount(5000)
                        .build();
        return storeRepository.save(store).getStoreId();
    }

    @Nested
    @DisplayName("역할 기반 권한(1차)")
    class RoleCheck {

        @Test
        @DisplayName("인증 없이 메뉴 등록을 시도하면 401을 반환한다")
        void createMenu_returns401_whenNoAuth() throws Exception {
            UUID storeId = createTestStore(1L);
            String body =
                    """
                    {"name":"김치찌개","description":"설명","price":8000,"aiGeneration":false}
                    """;

            mockMvc.perform(
                            post("/api/v1/stores/{storeId}/menus", storeId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(body))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("CUSTOMER 역할로 메뉴 등록을 시도하면 403을 반환한다")
        void createMenu_returns403_whenRoleIsCustomer() throws Exception {
            UUID storeId = createTestStore(1L);
            String token = issueAccessToken(Set.of(Role.CUSTOMER));
            String body =
                    """
                    {"name":"김치찌개","description":"설명","price":8000,"aiGeneration":false}
                    """;

            mockMvc.perform(
                            post("/api/v1/stores/{storeId}/menus", storeId)
                                    .header("Authorization", "Bearer " + token)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(body))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("소유권 검증(2차)")
    class OwnershipCheck {

        @Test
        @DisplayName("OWNER가 본인 소유가 아닌 가게에 메뉴를 등록하면 403을 반환한다")
        void createMenu_returns403_whenNotStoreOwner() throws Exception {
            Long realOwnerId = 500001L;
            UUID storeId = createTestStore(realOwnerId);
            // realOwnerId와 무관한, 새로 발급받은 OWNER 토큰(다른 사람의 가게를 건드리는 시나리오)
            String otherOwnerToken = issueAccessToken(Set.of(Role.OWNER));
            String body =
                    """
                    {"name":"김치찌개","description":"설명","price":8000,"aiGeneration":false}
                    """;

            mockMvc.perform(
                            post("/api/v1/stores/{storeId}/menus", storeId)
                                    .header("Authorization", "Bearer " + otherOwnerToken)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(body))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error").value("NOT_MENU_STORE_OWNER"));
        }

        @Test
        @DisplayName("MANAGER는 본인 소유가 아닌 가게에도 메뉴를 등록할 수 있다(우회)")
        void createMenu_returns201_whenManagerBypassesOwnership() throws Exception {
            UUID storeId = createTestStore(500002L);
            String managerToken = issueAccessToken(Set.of(Role.MANAGER));
            String body =
                    """
                    {"name":"된장찌개","description":"설명","price":9000,"aiGeneration":false}
                    """;

            mockMvc.perform(
                            post("/api/v1/stores/{storeId}/menus", storeId)
                                    .header("Authorization", "Bearer " + managerToken)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.name").value("된장찌개"));
        }
    }

    @Nested
    @DisplayName("역할별 응답 분기(3차) + 정보 비노출")
    class VisibilityCheck {

        @Test
        @DisplayName("숨김 메뉴를 무권한자가 조회하면 403이 아니라 404를 반환한다")
        void getMenu_returns404_notForbidden_whenHiddenAndNotOwner() throws Exception {
            UUID storeId = createTestStore(500003L);
            MenuEntity hiddenMenu =
                    menuRepository.save(new MenuEntity(storeId, "비밀메뉴", "설명", 7000));
            hiddenMenu.updateHidden(true);
            menuRepository.saveAndFlush(hiddenMenu);

            String customerToken = issueAccessToken(Set.of(Role.CUSTOMER));

            mockMvc.perform(
                            get("/api/v1/menus/{menuId}", hiddenMenu.getMenuId())
                                    .header("Authorization", "Bearer " + customerToken))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("MENU_NOT_FOUND"));
        }
    }
}
