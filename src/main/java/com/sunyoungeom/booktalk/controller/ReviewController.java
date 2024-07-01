package com.sunyoungeom.booktalk.controller;

import com.sunyoungeom.booktalk.domain.Review;
import com.sunyoungeom.booktalk.exception.ReviewException;
import com.sunyoungeom.booktalk.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

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
    public String write(HttpSession session, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        String title = (String) session.getAttribute("title");
        String referer = request.getHeader("Referer");
        Long userId = (Long) session.getAttribute("id");

        if (title != null) {
            if (referer != null && referer.contains("books/detail")) {
                Optional<Review> existingReview = reviewService.validateDuplicateReview(title, userId);
                if (existingReview.isPresent()) {
                    Long reviewId = existingReview.get().getId();
                    redirectAttributes.addFlashAttribute("review", existingReview);
                    return "redirect:/reviews/edit-init?id=" + reviewId;
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

    @GetMapping("/edit")
    public String edit() {
        return "reviews/edit";
    }

    @GetMapping("/edit-init")
    public String editInit(@RequestParam(name = "id", required = true) Long reviewId, RedirectAttributes redirectAttributes) {
        Review review = reviewService.existsById(reviewId);
        System.out.println(review.toString());
        redirectAttributes.addFlashAttribute("review", review);
        return "redirect:/reviews/edit";
    }

}