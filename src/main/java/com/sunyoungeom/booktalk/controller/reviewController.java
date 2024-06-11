package com.sunyoungeom.booktalk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reviews")
public class reviewController {

    @GetMapping("/list")
    public String list() {
        return "reviews/list";
    }

    @RequestMapping("/list/{title}")
    public String listSearch(@PathVariable String title, Model model) {
        model.addAttribute("title", title);
        return "reviews/list";
    }

    @GetMapping("/write/{title}")
    public String write(@PathVariable String title, Model model) {
        model.addAttribute("title", title);
        return "reviews/write";
    }
}
