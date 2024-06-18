package com.sunyoungeom.booktalk.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reviews")
@Slf4j
public class ReviewController {

    @GetMapping("/search")
    public String listSearchByTitle(@RequestParam(name = "title", required = false) String title, RedirectAttributes redirectAttributes) {
        if (title == null) {
            log.info("Review Search Title: None");
            return "/reviews/list";
        } else {
            log.info("Review Search Title: {}", title);
            redirectAttributes.addFlashAttribute("title", title);
        }
        return "redirect:/reviews/list";
    }

    @GetMapping("/list")
    public String list() {
        return "reviews/list";
    }

    @GetMapping("/write")
    public String write(HttpSession session, HttpServletRequest request, Model model) {
        Object title = session.getAttribute("title");
        String referer = request.getHeader("Referer");

        if (title != null) {
            if (referer != null && referer.contains("books/detail")) {
                log.info("Review Write Title: {}", title);
                model.addAttribute("title", title);
                return "reviews/write";
            } else {
                log.info("유효하지 않은 접근입니다. 메인 페이지로 이동합니다.");
                return "redirect:/";
            }
        }
        return "redirect:/";
    }
}