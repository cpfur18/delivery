package com.delivery.domain.user.repository;

import com.delivery.domain.user.entity.Role;
import com.delivery.domain.user.entity.User;
import com.delivery.domain.user.entity.UserStatus;
import com.delivery.global.exception.BusinessException;
import com.delivery.global.exception.GlobalErrorCode;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    /**
     * 권한으로 검색
     *
     * @param role
     * @return
     */
    public static Specification<User> equalRole(Role role) {
        return (root, query, criteriaBuilder) -> {
            if (role == null) return null;
            return criteriaBuilder.isMember(role, root.get("roles"));
        };
    }

    /**
     * 아이디로 검색
     *
     * @param username
     * @return
     */
    public static Specification<User> likeUsername(String username) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("username"), "%" + username + "%");
    }

    /**
     * 날짜 검색
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static Specification<User> rangeDate(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                throw new BusinessException(GlobalErrorCode.INVALID_DATE_RANGE);
            }
            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(
                        root.get("createdAt"),
                        startDate.atStartOfDay(),
                        endDate.atTime(LocalTime.MAX));
            }
            if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(
                        root.get("createdAt"), startDate.atStartOfDay());
            }
            if (endDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(
                        root.get("createdAt"), endDate.atTime(LocalTime.MAX));
            }
            return null;
        };
    }

    /**
     * 회원 상태 필터링
     *
     * @return
     */
    public static Specification<User> userStatus(UserStatus userStatus) {
        return (root, query, criteriaBuilder) ->
                (userStatus == null)
                        ? null
                        : criteriaBuilder.equal(root.get("userStatus"), userStatus);
    }
}
