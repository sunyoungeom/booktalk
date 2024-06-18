package com.sunyoungeom.booktalk.service;

import com.sunyoungeom.booktalk.domain.User;
import com.sunyoungeom.booktalk.exception.user.UserException;
import com.sunyoungeom.booktalk.exception.user.UserErrorCode;
import com.sunyoungeom.booktalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public User createUser(User user) {
        // 중복 유저 검증
        validateDuplicateUser(user);

        // 가입
        repository.save(user);
        return user;
    }

    private void validateDuplicateUser(User user) {
        repository.findById(user.getId())
                .ifPresent(m -> {
                    throw new UserException(UserErrorCode.USER_ALREADY_EXISTS_ERROR.getMessage());
                });
    }

    public User getUserById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_ERROR.getMessage()));
        return user;
    }

    public User updateReview(Long id, Map<String, Object> updates) {
        // 사용자 존재 확인
        User user = getUserById(id);

        repository.update(id, updates);
        return user;
    }

    public void deleteUser(Long id) {
        // 사용자 존재 확인
        User user = getUserById(id);

        repository.delete(id);
    }
}
