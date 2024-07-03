package com.sunyoungeom.booktalk.controller;

import com.sunyoungeom.booktalk.domain.User;
import com.sunyoungeom.booktalk.dto.UserUpdateDTO;
import com.sunyoungeom.booktalk.dto.UserDTO;
import com.sunyoungeom.booktalk.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/user")
@RequiredArgsConstructor

public class UserApiController {

    private final UserService service;
    private static final String FILE_DIRECTORY = "file/img/";

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        User createdUser = service.join(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/{id}")
        public ResponseEntity<Object> getUser(@PathVariable(name = "id") Long id) {
        UserDTO userDTO = service.getUserDTOById(id);
        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable(name = "id") Long id,
                                             @RequestBody UserUpdateDTO userUpdateDTO,
                                             HttpSession session) {
        log.info("api = {}", userUpdateDTO.toString());
        User updatedUser = service.updateUser(id, userUpdateDTO);
         if (userUpdateDTO.getNewNickname() != null) {
             session.setAttribute("username", userUpdateDTO.getNewNickname());
             return ResponseEntity.status(HttpStatus.OK).body(Map.of("nickname", updatedUser.getNickname()));
         } else if (userUpdateDTO.getCurrentPassword() != null && userUpdateDTO.getNewPassword() != null) {
             return ResponseEntity.status(HttpStatus.OK).body(Map.of("password", "비밀번호가 수정되었습니다."));
         }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "잘못된 요청입니다."));
    }
    @PatchMapping("/{id}/profileImg")
    public ResponseEntity<Object> updateProfileImg(@PathVariable(name = "id") Long id,
                                                   @RequestParam("file") MultipartFile file) {
        try {
            File directory = new File(FILE_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            User user = service.findById(id);
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
            service.updateUser(id, updateDTO);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("profileImgPath", profileImg));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "파일 업로드 중 오류가 발생하였습니다."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable(name = "id") Long id, HttpSession session) {
        service.deleteUser(id);
        session.invalidate();
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "회원탈퇴가 완료되었습니다."));
    }
}
