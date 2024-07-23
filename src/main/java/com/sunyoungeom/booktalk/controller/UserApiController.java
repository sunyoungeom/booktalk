package com.sunyoungeom.booktalk.controller;

import com.sunyoungeom.booktalk.common.ApiResponseUtil;
import com.sunyoungeom.booktalk.common.CustomApiResponse;
import com.sunyoungeom.booktalk.domain.User;
import com.sunyoungeom.booktalk.dto.*;
import com.sunyoungeom.booktalk.exception.UserException;
import com.sunyoungeom.booktalk.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserApiController {

    private final UserService userService;
    private static final String FILE_DIRECTORY = "/home/ubuntu/booktalk/file/img/";

    @PostMapping
    @Operation(summary = "회원 가입", description = "새로운 사용자를 등록하는 API입니다. " +
            "입력된 사용자 정보에 대해 유효성 검사를 수행하고, 이메일 및 닉네임의 중복 여부를 확인합니다.")
    @ApiResponse(responseCode = "400", description = "유효성 검사에서 오류가 발생하였습니다.")
    @ApiResponse(responseCode = "409", description = "이메일 또는 닉네임이 이미 사용중 입니다.")
    @ApiResponse(responseCode = "201", description = "회원 가입에 성공하였습니다.")
    public ResponseEntity<CustomApiResponse> createUser(@Valid @RequestBody UserJoinDTO user,
                                                        BindingResult bindingResult) {
        // 유효성 검사
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return ApiResponseUtil.validatedErrorResponse("유효성 검사 오류", bindingResult);
        }
        User createdUser = new User(user.getNickname(), user.getEmail(), user.getPassword());
        // 회원가입
        userService.join(createdUser);
        return ApiResponseUtil.successResponse(HttpStatus.CREATED, "회원가입 성공", createdUser);
    }

    @GetMapping("/{id}")
    @Operation(summary = "회원 조회", description = "회원을 조회하는 API입니다.")
    @ApiResponse(responseCode = "404", description = "해당 ID의 사용자가 존재하지 않습니다.")
    @ApiResponse(responseCode = "200", description = "회원 조회에 성공하였습니다.")
    public ResponseEntity<CustomApiResponse> getUser(@PathVariable(name = "id") Long id) {
        UserDTO userDTO = userService.getUserDTOById(id);
        return ApiResponseUtil.successResponse(HttpStatus.OK, "회원조회 성공", userDTO);
    }

    @PatchMapping("/{id}/nickname")
    @Operation(summary = "닉네임 수정", description = "사용자의 닉네임을 수정하는 API입니다. " +
            "입력된 닉네임에 대해 유효성 검사를 수행하고, 중복 여부를 확인합니다.")
    @ApiResponse(responseCode = "400", description = "유효성 검사에서 오류가 발생하였습니다.")
    @ApiResponse(responseCode = "403", description = "권한이 없는 사용자입니다.")
    @ApiResponse(responseCode = "404", description = "해당 ID의 사용자가 존재하지 않습니다.")
    @ApiResponse(responseCode = "409", description = "닉네임이 이미 사용중 입니다.")
    @ApiResponse(responseCode = "200", description = "닉네임 수정에 성공하였습니다.")
    public ResponseEntity<CustomApiResponse> updateNickname(@PathVariable(name = "id") Long id,
                                                            @Valid @RequestBody UserNicknameUpdateDTO nicknameUpdateDTO,
                                                            BindingResult bindingResult,
                                                            HttpServletRequest request) {
        // 유효성 검사
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return ApiResponseUtil.validatedErrorResponse("유효성 검사 오류", bindingResult);
        }
        // 닉네임 업데이트
        Long userId = (Long) request.getSession().getAttribute("userId");
        userService.updateNickname(userId, id, nicknameUpdateDTO.getNewNickname());
        request.getSession().setAttribute("username", nicknameUpdateDTO.getNewNickname());
        return ApiResponseUtil.successResponse(HttpStatus.OK, "닉네임이 수정되었습니다.", nicknameUpdateDTO.getNewNickname());
    }

    @PatchMapping("/{id}/password")
    @Operation(summary = "비밀번호 수정", description = "지정된 사용자 ID를 사용하여 사용자의 비밀번호를 수정하는 API입니다. " +
            "입력된 비밀번호에 대해 유효성 검사를 수행합니다.")
    @ApiResponse(responseCode = "400", description = "유효성 검사에서 오류가 발생하였습니다.")
    @ApiResponse(responseCode = "403", description = "권한이 없는 사용자입니다.")
    @ApiResponse(responseCode = "404", description = "해당 ID의 사용자가 존재하지 않습니다.")
    @ApiResponse(responseCode = "200", description = "비밀번호 수정에 성공하였습니다.")
    public ResponseEntity<CustomApiResponse> updatePassword(@PathVariable(name = "id") Long id,
                                                            @Valid @RequestBody UserPasswordUpdateDTO passwordUpdateDTO,
                                                            BindingResult bindingResult,
                                                            HttpServletRequest request) {
        // 유효성 검사
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return ApiResponseUtil.validatedErrorResponse("유효성 검사 오류", bindingResult);
        }
        // 비밀번호 업데이트
        Long userId = (Long) request.getSession().getAttribute("userId");
        userService.updatePassword(userId, id, passwordUpdateDTO.getCurrentPassword(), passwordUpdateDTO.getNewPassword());
        return ApiResponseUtil.successResponse(HttpStatus.OK, "비밀번호가 수정되었습니다.", null);
    }

    @PatchMapping("/{id}/profileImg")
    @Operation(summary = "프로필 사진 수정", description = "사용자의 프로필 사진을 수정하는 API입니다.")
    @ApiResponse(responseCode = "500", description = "업로드 중 오류가 발생하였습니다.")
    @ApiResponse(responseCode = "403", description = "권한이 없는 사용자입니다.")
    @ApiResponse(responseCode = "404", description = "해당 ID의 사용자가 존재하지 않습니다.")
    @ApiResponse(responseCode = "200", description = "프로필 사진 수정에 성공하였습니다.")
    public ResponseEntity<CustomApiResponse> updateProfileImg(@PathVariable(name = "id") Long id,
                                                              @RequestParam("file") MultipartFile file,
                                                              HttpServletRequest request) {
        // TODO: 업로드 로직 수정 필요
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

            String profileImg = "/file/img/" + fileName;
            // 프로필 사진 업데이트
            Long userId = (Long) request.getSession().getAttribute("userId");
            userService.updateProfile(userId, id, profileImg);

            return ApiResponseUtil.successResponse(HttpStatus.OK, "프로필 사진 수정에 성공하였습니다.", profileImg);
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponseUtil.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 중 오류가 발생하였습니다.");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "회원 탈퇴", description = "지정된 사용자 ID를 사용하여 사용자를 탈퇴시키는 API입니다. " +
            "현재 세션의 사용자가 요청한 경우에만 작동합니다.")
    @ApiResponse(responseCode = "403", description = "권한이 없는 사용자입니다.")
    @ApiResponse(responseCode = "404", description = "해당 ID의 사용자가 존재하지 않습니다.")
    @ApiResponse(responseCode = "200", description = "회원 탈퇴에 성공하였습니다.")
    public ResponseEntity<CustomApiResponse> deleteUser(@PathVariable(name = "id") Long id,
                                                        HttpServletRequest request) {
        // 회원탈퇴
        Long userId = (Long) request.getSession().getAttribute("userId");
        userService.deleteUser(userId, id);

        // 세션 무효화
        request.getSession().invalidate();
        return ApiResponseUtil.successResponse(HttpStatus.OK, "회원탈퇴가 완료되었습니다.", null);
    }
}
