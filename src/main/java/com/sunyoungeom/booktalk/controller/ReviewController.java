package com.sunyoungeom.booktalk.controller;

import com.sunyoungeom.booktalk.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/write")
    public String write(HttpSession session, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        String title = (String) session.getAttribute("title");
        String referer = request.getHeader("Referer");
        Long userId = (Long) session.getAttribute("id");

        // 제목 마지막 공백 제거
        title = title.replaceAll("\\s+$", "");

        if (title != null) {
            if (referer != null && referer.contains("books/detail")) {
                Integer reviewId = reviewService.validateDuplicateReview(userId, title);
                if (reviewId != null) {
                    return "redirect:/reviews/edit/" + reviewId;
                } else {
                    log.info("Review Write Title: {}", title);
                    model.addAttribute("title", title);
                    return "reviews/write";
                }
            } else {
                log.info("유효하지 않은 접근입니다. 메인 페이지로 이동합니다.");
                return "redirect:/";
            }
        }
        return "redirect:/";
    }

    @GetMapping("/search")
    public String listSearchByTitle(@RequestParam(name = "title", required = false) String title,
                                    @RequestParam(name = "author", required = false) String author,
                                    RedirectAttributes redirectAttributes) {
        if (title == null) {
            log.info("Review Search Title: None");
            return "/reviews/list";
        } else {
            log.info("Review Search Title: {}", title);
            redirectAttributes.addFlashAttribute("title", title);
            redirectAttributes.addFlashAttribute("author", author);
        }
        return "redirect:/reviews/list";
    }

    @GetMapping("/list")
    public String list() {
        return "reviews/list";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") Long reviewId, Model model) {
        model.addAttribute("reviewId", reviewId);
        return "reviews/edit";
    }
}