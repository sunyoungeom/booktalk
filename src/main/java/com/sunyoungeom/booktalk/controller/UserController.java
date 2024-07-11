package com.sunyoungeom.booktalk.controller;

import com.sunyoungeom.booktalk.dto.UserDTO;
import com.sunyoungeom.booktalk.dto.UserLoginDTO;
import com.sunyoungeom.booktalk.exception.UserException;
import com.sunyoungeom.booktalk.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService service;

    @GetMapping("/join")
    public String signup() {
        return "user/sign-up";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        // 오류 메시지가 있을 경우 화면에 출력
        if (model.containsAttribute("errorMessage")) {
            Object errorMessage = model.getAttribute("errorMessage");
            model.addAttribute("error", errorMessage);
        }
        return "user/sign-in";
    }

    @PostMapping("/login")
    public String login(@RequestParam(name = "redirectURL", defaultValue = "/") String redirectURL,
                        UserLoginDTO loginDto,
                        HttpServletRequest httpServletRequest,
                        RedirectAttributes redirectAttributes) {
        try {
            Long userId = service.login(loginDto);
            UserDTO userDTO = service.getUserDTOById(userId);

            // 로그인 성공시 새로운 세션 생성
            httpServletRequest.getSession().invalidate();
            HttpSession session = httpServletRequest.getSession(true);

            session.setAttribute("userId", userId);
            session.setAttribute("username", userDTO.getNickname());
            session.setAttribute("profileImgPath", userDTO.getProfileImgPath());

            // URL 인코딩
            String encodedRedirectURL = UriComponentsBuilder.fromUriString(redirectURL).build().encode().toUriString();
            return "redirect:" + encodedRedirectURL;

        } catch (UserException e) {
            // 오류 발생시 오류 메시지 전달
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);
        if(session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}