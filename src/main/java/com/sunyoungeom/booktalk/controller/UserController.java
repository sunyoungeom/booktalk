package com.sunyoungeom.booktalk.controller;

import com.sunyoungeom.booktalk.domain.User;
import com.sunyoungeom.booktalk.dto.LoginDTO;
import com.sunyoungeom.booktalk.dto.UserDTO;
import com.sunyoungeom.booktalk.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("/join")
    public String signup() {
        return "user/sign-up";
    }

    @GetMapping("/login")
    public String signin() {
        return "user/sign-in";
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginDTO loginDto, HttpSession session) {
        Long userId = service.login(loginDto);
        UserDTO userDTO = service.getUserDTOById(userId);

        session.setAttribute("id", userId);
        session.setAttribute("username", userDTO.getNickname());

        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}