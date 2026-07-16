package com.delivery.domain.user.dto.request;

import com.delivery.domain.user.entity.Role;
import com.delivery.domain.user.entity.UserStatus;
import java.time.LocalDate;

public record UserSearchRequest(
        Role role,
        UserStatus userStatus,
        String username,
        LocalDate startDate,
        LocalDate endDate) {}
