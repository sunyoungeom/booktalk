package com.sunyoungeom.booktalk.controller;

import com.sunyoungeom.booktalk.common.ApiResponseUtil;
import com.sunyoungeom.booktalk.domain.User;
import com.sunyoungeom.booktalk.dto.*;
import com.sunyoungeom.booktalk.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserApiController {

    private final UserService userService;
    private static final String FILE_DIRECTORY = "file/img/";

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserJoinDTO user,
                                             BindingResult bindingResult) {
        // 유효성 검사
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return ApiResponseUtil.validatedErrorResponse("유효성 검사 오류", bindingResult);
        }
        // 회원가입
        User createdUser = new User(user.getNickname(), user.getEmail(), user.getPassword());
        userService.join(createdUser);

        return ApiResponseUtil.successResponse(HttpStatus.CREATED, "회원가입 성공", createdUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable(name = "id") Long id) {
        UserDTO userDTO = userService.getUserDTOById(id);
        return ApiResponseUtil.successResponse(HttpStatus.OK, "회원조회 성공", userDTO);
    }

    @PatchMapping("/{id}/nickname")
    public ResponseEntity<Object> updateNickname(@PathVariable(name = "id") Long id,
                                                 @Valid @RequestBody UserNicknameUpdateDTO nicknameUpdateDTO,
                                                 BindingResult bindingResult,
                                                 HttpSession session) {
        // 유효성 검사
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return ApiResponseUtil.validatedErrorResponse("유효성 검사 오류", bindingResult);
        }
        // 닉네임 업데이트
        userService.updateNickname(id, nicknameUpdateDTO.getNewNickname());
        session.setAttribute("username", nicknameUpdateDTO.getNewNickname());

        return ApiResponseUtil.successResponse(HttpStatus.OK, "닉네임이 수정되었습니다.", nicknameUpdateDTO.getNewNickname());
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Object> updatePassword(@PathVariable(name = "id") Long id,
                                                 @Valid @RequestBody UserPasswordUpdateDTO passwordUpdateDTO,
                                                 BindingResult bindingResult) {
        // 유효성 검사
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return ApiResponseUtil.validatedErrorResponse("유효성 검사 오류", bindingResult);
        }
        // 비밀번호 업데이트
        userService.updatePassword(id, passwordUpdateDTO.getCurrentPassword(), passwordUpdateDTO.getNewPassword());

        return ApiResponseUtil.successResponse(HttpStatus.OK, "비밀번호가 수정되었습니다.", null);
    }

    @PatchMapping("/{id}/profileImg")
    public ResponseEntity<Object> updateProfileImg(@PathVariable(name = "id") Long id,
                                                   @RequestParam("file") MultipartFile file) {
        // TODO: 프로필 사진 유효성 검사 추가, 업로드 로직 수정 필요
        try {
            File directory = new File(FILE_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            User user = userService.findById(id);
            String profileImgPath = user.getProfileImgPath();

            File[] files = directory.listFiles();
            if (files != null && files.length > 0) {
                for (File elem : files) {
                    if (elem.getName().equals(profileImgPath)) {
                        boolean deleted = elem.delete();
                        if (deleted) {
                            log.info("기존 파일 삭제 완료: {}", elem.getName());
                        } else {
                            log.error("기존 파일 삭제 실패: {}", elem.getName());
                        }
                    }
                }
            }

            String uuid = UUID.randomUUID().toString();
            String fileName = uuid + "_" + file.getOriginalFilename();

            byte[] bytes = file.getBytes();
            Path filePath = Paths.get(FILE_DIRECTORY + fileName);
            Files.write(filePath, bytes);

            UserUpdateDTO updateDTO = new UserUpdateDTO();
            String profileImg = "/file/img/" + fileName;
            updateDTO.setProfileImgPath(profileImg);
            userService.updateUser(id, updateDTO);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("profileImgPath", profileImg));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "파일 업로드 중 오류가 발생하였습니다."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable(name = "id") Long id, HttpSession session) {
        // 회원탈퇴
        userService.deleteUser(id);
        // 세션 무효화
        session.invalidate();
        return ApiResponseUtil.successResponse(HttpStatus.OK, "회원탈퇴가 완료되었습니다.", null);
    }
}
