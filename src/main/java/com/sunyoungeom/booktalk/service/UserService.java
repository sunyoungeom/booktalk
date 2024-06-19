package com.sunyoungeom.booktalk.service;

import com.sunyoungeom.booktalk.domain.User;
import com.sunyoungeom.booktalk.exception.UserException;
import com.sunyoungeom.booktalk.exception.UserErrorCode;
import com.sunyoungeom.booktalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public User join(User user) {
        // 중복 유저 검증
        validateDuplicateUser(user);

        // 가입
        repository.save(user);
        return user;
    }

    private void validateDuplicateUser(User user) {
        boolean nicknameExists = repository.existsByNickname(user.getNickname());
        boolean emailExists = repository.existsByEmail(user.getEmail());

        if (nicknameExists) {
            throw new UserException(UserErrorCode.NICKNAME_ALREADY_EXISTS_ERROR.getMessage());
        } else if (emailExists) {
            throw new UserException(UserErrorCode.EMAIL_ALREADY_EXISTS_ERROR.getMessage());
        }
    }

    public User findById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_ERROR.getMessage()));
        return user;
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User updateUser(Long id, Map<String, Object> updates) {
        // 사용자 존재 확인
        User user = findById(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "nickname":
                    user.setNickname((String) value);
                    break;
                case "password":
                    user.setPassword((String) value);
                    break;
                default:
                    throw new UserException(UserErrorCode.IMMUTABLE_USER_FIELD.getMessage());
            }
        });

        repository.update(id, user);
        return user;
    }

    public void deleteUser(Long id) {
        // 사용자 존재 확인
        User user = findById(id);

        repository.delete(id);
    }
}
