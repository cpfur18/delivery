package com.delivery.domain.store.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.delivery.config.AbstractIntegrationTest;
import com.delivery.domain.store.dto.request.StoreRequest;
import com.delivery.domain.store.dto.response.StoreResponse;
import com.delivery.domain.store.entity.Category;
import com.delivery.domain.store.entity.Region;
import com.delivery.domain.store.exception.StoreException;
import com.delivery.domain.store.repository.CategoryRepository;
import com.delivery.domain.store.repository.RegionRepository;
import java.util.UUID;

import com.delivery.domain.user.UserDeletedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
        properties = {
            "gemini.api-key=test-dummy-key",
            "gemini.base-url=https://generativelanguage.googleapis.com",
            "gemini.model=gemini-1.5-flash"
        })
class StoreServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired private StoreService storeService;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private RegionRepository regionRepository;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private Category savedCategory;
    private Region savedRegion;

    @BeforeEach
    void setUp() {
        savedCategory = categoryRepository.save(Category.builder().name("한식").build());
        savedRegion =
                regionRepository.save(
                        Region.builder().name("강남구").latitude(37.5).longitude(127.0).build());
        savedRegion = regionRepository.save(
                Region.builder().name("강남구").latitude(37.5).longitude(127.0).build());
    }

    private StoreRequest defaultRequest() {
        return new StoreRequest(
                savedCategory.getCategoryId(),
                savedRegion.getRegionId(),
                "테스트 가게",
                "서울시 강남구",
                "01012345678",
                "테스트 가게입니다",
                10000);
    }

    private StoreResponse createDefaultStore() {
        return storeService.createStore(OWNER_ID, defaultRequest());
    }

    @Nested
    @DisplayName("가게 등록")
    class CreateStore {

        @Test
        @DisplayName("정상적으로 가게가 등록된다.")
        void createStore_success() {
            StoreResponse response = storeService.createStore(OWNER_ID, defaultRequest());

            assertThat(response.name()).isEqualTo("테스트 가게");
            assertThat(response.address()).isEqualTo("서울시 강남구");
            assertThat(response.isOpen()).isFalse();
            assertThat(response.averageRating()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("중복된 가게 등록 시 예외가 발생한다.")
        void createStore_fail_when_duplicate() {
            storeService.createStore(OWNER_ID, defaultRequest());

            assertThatThrownBy(() -> storeService.createStore(OWNER_ID, defaultRequest()))
                    .isInstanceOf(StoreException.class)
                    .hasMessage("이미 등록된 가게입니다.");
        }
    }

    @Nested
    @DisplayName("가게 조회")
    class GetStore {

        @Test
        @DisplayName("가게 단건 조회에 성공한다.")
        void getStore_success() {
            StoreResponse created = createDefaultStore();

            StoreResponse found = storeService.getStore(created.storeId());

            assertThat(found.storeId()).isEqualTo(created.storeId());
            assertThat(found.name()).isEqualTo("테스트 가게");
        }

        @Test
        @DisplayName("존재하지 않는 가게 조회 시 예외가 발생한다.")
        void getStore_fail_when_not_found() {
            assertThatThrownBy(() -> storeService.getStore(UUID.randomUUID()))
                    .isInstanceOf(StoreException.class)
                    .hasMessage("가게를 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("가게 검색 (QueryDSL)")
    class SearchStores {

        @Test
        @DisplayName("필터 없이 전체 가게를 조회한다.")
        void searchStores_no_filter() {
            storeService.createStore(OWNER_ID, defaultRequest());
            storeService.createStore(OWNER_ID, new StoreRequest(
                    savedCategory.getCategoryId(), savedRegion.getRegionId(),
                    "다른 가게", "서울시 서초구", "01098765432", null, 5000));

            Page<StoreResponse> result = storeService.getStores(null, null, null, PageRequest.of(0, 10));

            assertThat(result.getTotalElements()).isGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("이름으로 가게를 검색한다.")
        void searchStores_by_name() {
            storeService.createStore(OWNER_ID, defaultRequest());
            storeService.createStore(OWNER_ID, new StoreRequest(
                    savedCategory.getCategoryId(), savedRegion.getRegionId(),
                    "다른 가게", "서울시 서초구", "01098765432", null, 5000));

            Page<StoreResponse> result = storeService.getStores(null, null, "테스트", PageRequest.of(0, 10));

            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().get(0).name()).isEqualTo("테스트 가게");
        }

        @Test
        @DisplayName("카테고리로 가게를 필터링한다.")
        void searchStores_by_category() {
            Category otherCategory = categoryRepository.save(Category.builder().name("중식").build());

            storeService.createStore(OWNER_ID, defaultRequest());
            storeService.createStore(OWNER_ID, new StoreRequest(
                    otherCategory.getCategoryId(), savedRegion.getRegionId(),
                    "중식 가게", "서울시 서초구", "01098765432", null, 5000));

            Page<StoreResponse> result = storeService.getStores(
                    savedCategory.getCategoryId(), null, null, PageRequest.of(0, 10));

            assertThat(result.getContent()).allMatch(s -> s.categoryId().equals(savedCategory.getCategoryId()));
        }

        @Test
        @DisplayName("지역으로 가게를 필터링한다.")
        void searchStores_by_region() {
            Region otherRegion = regionRepository.save(Region.builder().name("부산").build());

            storeService.createStore(OWNER_ID, defaultRequest());
            storeService.createStore(OWNER_ID, new StoreRequest(
                    savedCategory.getCategoryId(), otherRegion.getRegionId(),
                    "부산 가게", "부산시 해운대구", "01011112222", null, 5000));

            Page<StoreResponse> result = storeService.getStores(
                    null, savedRegion.getRegionId(), null, PageRequest.of(0, 10));

            assertThat(result.getContent()).allMatch(s -> s.regionId().equals(savedRegion.getRegionId()));
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 페이지를 반환한다.")
        void searchStores_no_result() {
            Page<StoreResponse> result = storeService.getStores(null, null, "없는가게이름xyz", PageRequest.of(0, 10));

            assertThat(result.getTotalElements()).isEqualTo(0);
        }

        @Test
        @DisplayName("회원 탈퇴 이벤트가 발생해도 가게는 삭제되지 않는다.")
        void store_persists_after_user_deletion() {
            storeService.createStore(OWNER_ID, defaultRequest());

            applicationEventPublisher.publishEvent(new UserDeletedEvent(OWNER_ID, "testuser"));

            Page<StoreResponse> result = storeService.getStores(null, null, null, PageRequest.of(0, 10));
            assertThat(result.getContent()).anyMatch(s -> s.name().equals("테스트 가게"));
        }
    }

    @Test
    @Transactional
    @DisplayName("가게 등록 성공")
    void createStore_success() {
        // given
        StoreRequest request =
                new StoreRequest(
                        savedCategory.getCategoryId(),
                        savedRegion.getRegionId(),
                        "테스트 가게",
                        "서울시 강남구",
                        "01012345678",
                        "테스트 가게입니다",
                        10000);

        // when
        StoreResponse response = storeService.createStore(1L, request);

        // then
        assertThat(response.name()).isEqualTo("테스트 가게");
        assertThat(response.address()).isEqualTo("서울시 강남구");
        assertThat(response.isOpen()).isFalse();
        assertThat(response.averageRating()).isEqualTo(0.0);
    }

    @Test
    @Transactional
    @DisplayName("중복된 가게 등록 시 예외가 발생해야 한다.")
    void createStore_fail_when_duplicate() {
        // given
        StoreRequest request =
                new StoreRequest(
                        savedCategory.getCategoryId(),
                        savedRegion.getRegionId(),
                        "테스트 가게",
                        "서울시 강남구",
                        "01012345678",
                        "테스트",
                        10000);

        storeService.createStore(1L, request);

        // when & then
        assertThatThrownBy(() -> storeService.createStore(1L, request))
                .isInstanceOf(StoreException.class)
                .hasMessage("이미 등록된 가게입니다.");
    }

    @Test
    @DisplayName("존재하지 않는 가게 조회 시 예외가 발생해야 한다.")
    void getStore_fail_when_not_found() {
        // given
        UUID randomId = UUID.randomUUID();

        // when & then
        assertThatThrownBy(() -> storeService.getStore(randomId))
                .isInstanceOf(StoreException.class)
                .hasMessage("가게를 찾을 수 없습니다.");
    }
}
