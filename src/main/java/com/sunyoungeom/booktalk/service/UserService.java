package com.sunyoungeom.booktalk.service;

import com.sunyoungeom.booktalk.domain.User;
import com.sunyoungeom.booktalk.domain.UserSignupType;
import com.sunyoungeom.booktalk.dto.UserDTO;
import com.sunyoungeom.booktalk.dto.UserLoginDTO;
import com.sunyoungeom.booktalk.exception.UserException;
import com.sunyoungeom.booktalk.exception.UserErrorCode;
import com.sunyoungeom.booktalk.exception.common.CommonErrorCode;
import com.sunyoungeom.booktalk.repository.ReviewRepository;
import com.sunyoungeom.booktalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public User join(User user) {
        // 중복회원 검증
        validateDuplicateUser(user);
        // 회원가입
        user.setSignUpType(UserSignupType.EMAIL.getTypeName());
        userRepository.save(user);

        return user;
    }

    private void validateDuplicateUser(User user) {
        boolean nicknameExists = userRepository.existsByNickname(user.getNickname());
        boolean emailExists = userRepository.existsByEmail(user.getEmail());

        if (nicknameExists) {
            throw new UserException(UserErrorCode.NICKNAME_ALREADY_EXISTS_ERROR);
        } else if (emailExists) {
            throw new UserException(UserErrorCode.EMAIL_ALREADY_EXISTS_ERROR);
        }
    }

    public Long login(UserLoginDTO loginDto) {
        User user = userRepository.findIdByEmail(loginDto.getEmail())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_ERROR));

        if (user.getPassword().equals(loginDto.getPassword())) {
            return user.getId();
        } else {
            throw new UserException(UserErrorCode.INVALID_PASSWORD_ERROR);
        }
    }

    public User findIdByEmail(String email) {
        User user = userRepository.findIdByEmail(email)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_ERROR));
        return user;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void updateNickname(Long sessionId, Long userId, String newNickname) {
        // 회원 조회
        User user = findById(userId);
        // 권한 확인
        validateUserAuthorization(sessionId, userId);
        // 닉네임 중복검사
        if (userRepository.existsByNickname(newNickname)) {
            throw new UserException(UserErrorCode.NICKNAME_ALREADY_EXISTS_ERROR);
        }
        // 닉네임 업데이트
        reviewRepository.updateAuthor(user.getId(), newNickname);
        user.setNickname(newNickname);
        userRepository.update(userId, user);
    }

    public void updatePassword(Long sessionId, Long userId, String currentPassword, String newPassword) {
        // 회원조회
        User user = findById(userId);
        // 권한 확인
        validateUserAuthorization(sessionId, userId);
        // 비밀번호 일치 검사
        if (user.getPassword().equals(currentPassword)) {
            user.setPassword(newPassword);
        } else {
            throw new UserException(UserErrorCode.INVALID_PASSWORD_ERROR);
        }
        // 비밀번호 업데이트
        userRepository.update(userId, user);
    }

    public void updateProfile(Long sessionId, Long userId, String profileImgPath) {
        // 회원조회
        User user = findById(userId);
        // 권한 확인
        validateUserAuthorization(sessionId, userId);
        // 프로필 사진 업데이트
        user.setProfileImgPath(profileImgPath);
        userRepository.update(userId, user);
    }

    public void deleteUser(Long sessionId, Long userId) {
        // 권한 확인
        validateUserAuthorization(sessionId, userId);
        findById(userId);
        userRepository.delete(userId);
    }

    public void validateUserAuthorization(Long sessionId, Long userId) {
        if (sessionId != userId) {
//            throw new UserException(CommonErrorCode.ACCESS_DENIED_ERROR.getMessage());
        }
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
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_ERROR));
    }
}
