package com.sunyoungeom.booktalk.controller;

import com.sunyoungeom.booktalk.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reviews")
public class reviewController {

    @GetMapping("/list")
    public String list() {
        return "reviews/list";
    }

    @GetMapping("/list/{title}/popular")
    public String listSearchByPopular(@PathVariable String title, Model model) {
        model.addAttribute("title", title);
        model.addAttribute("sort", Sort.POPULAR);
        return "reviews/list";
    }

    @GetMapping("/list/{title}/latest")
    public String listSearchByLatest(@PathVariable String title, Model model) {
        model.addAttribute("title", title);
        model.addAttribute("sort", Sort.LATEST);
        return "reviews/list";
    }

    @GetMapping("/write/{title}")
    public String write(@PathVariable String title, Model model) {
        model.addAttribute("title", title);
        return "reviews/write";
    }


}
