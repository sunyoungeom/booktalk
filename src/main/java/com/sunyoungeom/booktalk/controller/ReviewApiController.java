package com.sunyoungeom.booktalk.controller;

import com.sunyoungeom.booktalk.domain.Review;
import com.sunyoungeom.booktalk.dto.ReviewLikesDTO;
import com.sunyoungeom.booktalk.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewApiController {

    private final ReviewService reviewService;
    private final HttpSession session;

    @PostMapping
    public ResponseEntity<Object> createReview(@RequestBody Review review) {
        Long userId = (Long) session.getAttribute("id");
        String author = (String) session.getAttribute("username");
        Review createdReview = reviewService.createReview(review, userId, author);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @GetMapping
    public ResponseEntity<Object> getReviews(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "author", required = false) String author,
            @RequestParam(name = "sortBy", required = false) String sortBy) {

        Long userId = (Long) session.getAttribute("id");
        List<Review> reviews;

        // 리뷰 검색
        if (title != null) {
            // 제목별 조회
            if ("popularity".equalsIgnoreCase(sortBy)) {
                reviews = reviewService.findByTitleOrderByLikesDesc(title);
            } else {
                reviews = reviewService.findByTitleOrderByDateDesc(title);
            }
        } else {
            // 모든 글 조회
            if ("popularity".equalsIgnoreCase(sortBy)) {
                reviews = reviewService.findAllOrderByLikesDesc();
                log.info("test" + reviews.toString());
            } else {
                reviews = reviewService.findAllOrderByDateDesc();
                log.info("test2" + reviews.toString());
            }
        }

        // 로그인한 유저 작성 리뷰 조회
        if (author != null) {
            reviews = reviewService.findByUserId(userId);
        }

        if (!reviews.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("reviews", reviews));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "리뷰 검색결과가 없습니다."));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateReview(@PathVariable(name = "id") Long reviewId, @RequestBody String content) {
        Long userId = (Long) session.getAttribute("id");
        String author = (String) session.getAttribute("username");
        reviewService.update(reviewId, userId, author);
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteReview(@PathVariable(name = "id") Long reviewId) {
        Long userId = (Long) session.getAttribute("id");
        String author = (String) session.getAttribute("username");
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of("message", "리뷰가 성공적으로 삭제되었습니다."));
    }

    @PostMapping("/{id}/likes")
    public ResponseEntity<Object> likeReview(@PathVariable(name = "id") Long reviewId) {
        Long userId = (Long) session.getAttribute("id");
        ReviewLikesDTO reviewLikesDTO = reviewService.likeReview(reviewId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(reviewLikesDTO);
    }
}
