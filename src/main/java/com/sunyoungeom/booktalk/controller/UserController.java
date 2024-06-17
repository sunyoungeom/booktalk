package com.sunyoungeom.booktalk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping("/join")
    public String signup() {
        return "user/sign-up";
    }
    @GetMapping("/login")
    public String signin() {
        return "user/sign-in";
    }

}
