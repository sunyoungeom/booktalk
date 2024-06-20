package com.sunyoungeom.booktalk.service;

import com.sunyoungeom.booktalk.domain.User;
import com.sunyoungeom.booktalk.domain.UserRole;
import com.sunyoungeom.booktalk.domain.UserSignupType;
import com.sunyoungeom.booktalk.dto.LoginDTO;
import com.sunyoungeom.booktalk.dto.UpdateDTO;
import com.sunyoungeom.booktalk.exception.UserException;
import com.sunyoungeom.booktalk.exception.UserErrorCode;
import com.sunyoungeom.booktalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        user.setUserRole(UserRole.USER);
        user.setSignUpType(UserSignupType.EMAIL.getTypeName());
        user.setSignUpDate(LocalDate.now());
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

    public Long login(LoginDTO loginDto) {
        User user = repository.findIdByEmail(loginDto.getEmail()).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_ERROR.getMessage()));
        if (user.getPassword().equals(loginDto.getPassword())) {
        return user.getId();
        } else if (!user.getPassword().equals(loginDto.getPassword())) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD_ERROR.getMessage());
        }
        return -1L;
    }

    public User findById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_ERROR.getMessage()));
        return user;
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User updateUser(Long id, UpdateDTO updateDTO) {
        // 사용자 존재 확인
        User user = findById(id);

        if (updateDTO.getNickname() != null) {
            user.setNickname(updateDTO.getNickname());
        }
        if (updateDTO.getCurrentPassword() != null && updateDTO.getNewPassword() != null) {
            if (user.getPassword().equals(updateDTO.getCurrentPassword())) {
            user.setPassword(updateDTO.getNewPassword());
            } else {
                throw new UserException(UserErrorCode.INVALID_PASSWORD_ERROR.getMessage());
            }
        }

        repository.update(id, user);
        System.out.println(user.toString());
        return user;
    }

    public void deleteUser(Long id) {
        // 사용자 존재 확인
        User user = findById(id);

        repository.delete(id);
    }
}
