package com.sunyoungeom.booktalk.controller;

import com.sunyoungeom.booktalk.domain.Review;
import com.sunyoungeom.booktalk.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewApiController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Object> createReview(@RequestBody Review review) {
        Review createdReview = reviewService.createReview(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @GetMapping
    public ResponseEntity<Object> getReviews(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "author", required = false) String author,
            @RequestParam(name = "sortBy", required = false) String sortBy) {

        List<Review> reviews;

        if (title != null) {
            // 제목별 조회
            if ("popularity".equalsIgnoreCase(sortBy)) {
                reviews = reviewService.findByTitleSortedByLikes(title);
            } else {
                reviews = reviewService.findByTitleSortedByDate(title);
            }
        } else if (author != null) {
            // 작성자별 조회
            reviews = reviewService.findByAuthor(author);
        } else {
            // 모든 글 조회
            if ("popularity".equalsIgnoreCase(sortBy)) {
                reviews = reviewService.findAllSortedByLikes();
                log.info("test" + reviews.toString());
            } else {
                reviews = reviewService.findAllSortedBydate();
                log.info("test2" + reviews.toString());
            }
        }

        if (!reviews.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("reviews", reviews));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "리뷰 검색결과가 없습니다."));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateReview(@PathVariable(name = "id") Long id, @RequestBody String content) {
        Review updatedReview = reviewService.updateReview(id, content);
        return ResponseEntity.status(HttpStatus.FOUND).body(updatedReview);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteReview(@PathVariable(name = "id") Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of("message", "리뷰가 성공적으로 삭제되었습니다."));
    }
}
