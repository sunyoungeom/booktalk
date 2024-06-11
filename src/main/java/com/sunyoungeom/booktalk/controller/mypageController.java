package com.sunyoungeom.booktalk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
public class mypageController {

    @GetMapping("/info")
    public String info() {
        return "mypage/info";
    }

    @GetMapping("/reviews/my")
    public String myReview() {

        return "mypage/reviews-my";
    }

    @GetMapping("/reviews/like")
    public String likeReview() {
        return "mypage/reviews-like";

    }

}
