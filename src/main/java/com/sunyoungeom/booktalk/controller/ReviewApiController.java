package com.sunyoungeom.booktalk.controller;

import com.sunyoungeom.booktalk.domain.Review;
import com.sunyoungeom.booktalk.dto.ReviewDTO;
import com.sunyoungeom.booktalk.dto.ReviewLikesDTO;
import com.sunyoungeom.booktalk.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(name = "sortBy", required = false) String sortBy,
            Pageable pageable) {

        Long userId = (Long) session.getAttribute("id");

        if (author == null) {
            Page<ReviewDTO> reviews = reviewService.findReviewsWithLikeStatus(userId, title, sortBy, pageable);
            if (!reviews.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(Map.of("reviews", reviews));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "리뷰 검색결과가 없습니다."));
            }

        } else {
            Page<ReviewDTO> reviews = reviewService.findByUserId(userId, pageable);
            if (!reviews.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(Map.of("reviews", reviews));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "리뷰 검색결과가 없습니다."));
            }
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateReview(@PathVariable(name = "id") Long reviewId, @RequestBody ReviewDTO reviewDTO) {
        Long userId = (Long) session.getAttribute("id");
        reviewService.update(reviewId, userId, reviewDTO.getContent());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteReview(@PathVariable(name = "id") Long reviewId) {
        Long userId = (Long) session.getAttribute("id");
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
