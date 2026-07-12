package com.delivery.domain.user.service;

import com.delivery.domain.user.dto.request.UpdateUserRequest;
import com.delivery.domain.user.dto.response.UpdatedUserResponse;
import com.delivery.domain.user.dto.response.UserValidationResponse;
import com.delivery.domain.user.response.UserResponse;
import com.delivery.domain.user.entity.User;
import com.delivery.domain.user.exception.UserErrorCode;
import com.delivery.domain.user.exception.UserException;
import com.delivery.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

//    @Transactional(readOnly = true)
//    public UserResponse findUserInfo(Long userId) {
//        return userRepository
//                .findByIdAndDeletedAtIsNull(userId)
//                .orElseThrow(() -> new UserException(UserErrorCode.NOT_EXIST_USER));
//    }

    public UpdatedUserResponse updateUser(Long id, @Valid UpdateUserRequest request) {
        throw new UnsupportedOperationException("개발 중 입니다.");
    }

    public void deleteUser(Long id) {
        throw new UnsupportedOperationException("개발 중 입니다.");
    }

    public UserValidationResponse isDuplicationId(String username) {
        throw new UnsupportedOperationException("개발 중 입니다.");
    }

    public UserValidationResponse isDuplicationNickname(String nickName) {
        throw new UnsupportedOperationException("개발 중 입니다.");
    }
}
