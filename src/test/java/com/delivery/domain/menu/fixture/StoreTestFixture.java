package com.delivery.domain.menu.fixture;

import com.delivery.domain.store.entity.Store;
import java.util.UUID;
import lombok.AllArgsConstructor;

/** Menu/AI 통합 테스트에서 소유권 검증용 가게를 만들 때 사용 */
@AllArgsConstructor
public enum StoreTestFixture {
    DEFAULT("서울시 강남구 테스트로 1", "01012345678", 5000);

    private final String address;
    private final String phone;
    private final int minOrderAmount;

    public Store createStore(Long ownerId) {
        return Store.builder()
                .userId(ownerId)
                .categoryId(UUID.randomUUID())
                .regionId(UUID.randomUUID())
                .name("테스트가게" + UUID.randomUUID())
                .address(address)
                .phone(phone)
                .minOrderAmount(minOrderAmount)
                .build();
    }
}
