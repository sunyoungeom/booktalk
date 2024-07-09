package com.sunyoungeom.booktalk.service;

import com.sunyoungeom.booktalk.domain.User;
import com.sunyoungeom.booktalk.domain.UserSignupType;
import com.sunyoungeom.booktalk.dto.UserDTO;
import com.sunyoungeom.booktalk.dto.UserLoginDTO;
import com.sunyoungeom.booktalk.dto.UserUpdateDTO;
import com.sunyoungeom.booktalk.exception.UserException;
import com.sunyoungeom.booktalk.exception.UserErrorCode;
import com.sunyoungeom.booktalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public User join(User user) {
        // 중복회원 검증
        validateDuplicateUser(user);
        // 회원가입
        user.setSignUpType(UserSignupType.EMAIL.getTypeName());
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

    public Long login(UserLoginDTO loginDto) {
        User user = repository.findIdByEmail(loginDto.getEmail())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_ERROR.getMessage()));

        if (user.getPassword().equals(loginDto.getPassword())) {
            return user.getId();
        } else {
            throw new UserException(UserErrorCode.INVALID_PASSWORD_ERROR.getMessage());
        }
    }

    public User findIdByEmail(String email) {
        User user = repository.findIdByEmail(email)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_ERROR.getMessage()));
        return user;
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public void updateNickname(Long id, String newNickname) {
        // 회원조회
        User user = findById(id);
        // 닉네임 중복검사
        if (repository.existsByNickname(newNickname)) {
            throw new UserException(UserErrorCode.NICKNAME_ALREADY_EXISTS_ERROR.getMessage());
        }
        // 닉네임 업데이트
        user.setNickname(newNickname);
        repository.update(id, user);
    }

    public void updatePassword(Long id, String currentPassword, String newPassword) {
        // 회원조회
        User user = findById(id);
        // 비밀번호 일치 검사
        if (user.getPassword().equals(currentPassword)) {
            user.setPassword(newPassword);
        } else {
            throw new UserException(UserErrorCode.INVALID_PASSWORD_ERROR.getMessage());
        }
        // 비밀번호 업데이트
        repository.update(id, user);
    }

    public User updateUser(Long id, UserUpdateDTO updateDTO) {
        User user = findById(id);
        if (updateDTO.getProfileImgPath() != null) {
            user.setProfileImgPath(updateDTO.getProfileImgPath());
        }
        if (updateDTO.getNewNickname() != null) {
            if (repository.existsByNickname(updateDTO.getNewNickname())) {
                throw new UserException(UserErrorCode.NICKNAME_ALREADY_EXISTS_ERROR.getMessage());
            }
            user.setNickname(updateDTO.getNewNickname());
        }
        if (updateDTO.getCurrentPassword() != null && updateDTO.getNewPassword() != null) {
            if (user.getPassword().equals(updateDTO.getCurrentPassword())) {
                user.setPassword(updateDTO.getNewPassword());
            } else {
                throw new UserException(UserErrorCode.INVALID_PASSWORD_ERROR.getMessage());
            }
        }

        repository.update(id, user);
        return user;
    }

    public void deleteUser(Long id) {
        findById(id);
        repository.delete(id);
    }

    public String getNicknameById(Long id) {
        if (id == null) {
            return null;
        }
        User user = repository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }

        return user.getNickname();
    }

    public UserDTO getUserDTOById(Long id) {
        User user = findById(id);

        UserDTO userDTO = new UserDTO();
        userDTO.setProfileImgPath(user.getProfileImgPath());
        userDTO.setNickname(user.getNickname());
        userDTO.setEmail(user.getEmail());
        userDTO.setSignUpType(user.getSignUpType());
        userDTO.setSignUpDate(user.getSignUpDate());

        return userDTO;
    }

    public User findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_ERROR.getMessage()));
    }
}
