package com.sunyoungeom.booktalk.controller;

import com.sunyoungeom.booktalk.domain.User;
import com.sunyoungeom.booktalk.dto.UserUpdateDTO;
import com.sunyoungeom.booktalk.dto.UserDTO;
import com.sunyoungeom.booktalk.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor

public class UserApiController {

    private final UserService service;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        User createdUser = service.join(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/{id}")
        public ResponseEntity<Object> getUser(@PathVariable(name = "id") Long id) {
        User user = service.findById(id);

        UserDTO userDTO = new UserDTO();
        userDTO.setProfileImgPath(user.getProfileImgPath());
        userDTO.setNickname(user.getNickname());
        userDTO.setEmail(user.getEmail());
        userDTO.setSignUpType(user.getSignUpType());
        userDTO.setSignUpDate(user.getSignUpDate());

        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable(name = "id") Long id, @RequestBody UserUpdateDTO userUpdateDTO) {
        log.info("api = {}", userUpdateDTO.toString());
        User updatedUser = service.updateUser(id, userUpdateDTO);
         if (userUpdateDTO.getNewNickname() != null) {
             return ResponseEntity.status(HttpStatus.OK).body(Map.of("nickname", updatedUser.getNickname()));
         } else if (userUpdateDTO.getCurrentPassword() != null && userUpdateDTO.getNewPassword() != null) {
             return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "비밀번호가 수정되었습니다."));
         }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "잘못된 요청입니다."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable(name = "id") Long id) {
        service.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "회원탈퇴가 완료되었습니다."));
    }
}
