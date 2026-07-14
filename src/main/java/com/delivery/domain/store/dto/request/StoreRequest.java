package com.delivery.domain.store.dto.request;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record StoreRequest(
        @NotNull(message = "REQUIRED_VALUE")
        UUID categoryId,

        @NotNull(message = "REQUIRED_VALUE")
        UUID regionId,

        @NotBlank(message = "REQUIRED_VALUE")
        @Size(min = 1, max = 50, message = "가게 이름은 1~50자 이내여야 합니다.")
        String name,

        @NotBlank(message = "REQUIRED_VALUE")
        @Size(max = 255, message = "주소는 255자 이내여야 합니다.")
        String address,

        @NotBlank(message = "REQUIRED_VALUE")
        @Size(max = 20, message = "전화번호는 20자 이내여야 합니다.")
        String phone,

        @Size(max = 500, message = "설명은 500자 이내여야 합니다.")
        String description,

        @NotNull(message = "REQUIRED_VALUE")
        @Min(value = 0, message = "최소 주문 금액은 0 이상이어야 합니다.")
        Integer minOrderAmount
) {}