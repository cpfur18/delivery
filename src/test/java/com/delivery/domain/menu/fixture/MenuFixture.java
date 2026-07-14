package com.delivery.domain.menu.fixture;

import com.delivery.domain.menu.dto.request.CreateMenuRequest;
import com.delivery.domain.menu.dto.request.UpdateMenuRequest;
import com.delivery.domain.menu.entity.MenuEntity;
import java.util.UUID;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MenuFixture {
    CREATE("김치찌개", "설명", 8000),
    UPDATE("된장찌개", "새 설명", 9000);

    private final String name;
    private final String description;
    private final int price;

    public MenuEntity createEntity(UUID storeId) {
        return new MenuEntity(storeId, name, description, price);
    }

    public CreateMenuRequest createRequestDto() {
        return new CreateMenuRequest(name, description, price, false, null);
    }

    public UpdateMenuRequest updateRequestDto() {
        return new UpdateMenuRequest(name, description, price);
    }

    public String menuName() {
        return name;
    }

    public String description() {
        return description;
    }

    public int price() {
        return price;
    }
}
