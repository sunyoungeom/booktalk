package com.sunyoungeom.booktalk.controller;

import com.sunyoungeom.booktalk.common.ApiResponseUtil;
import com.sunyoungeom.booktalk.domain.Review;
import com.sunyoungeom.booktalk.dto.ReviewAddDTO;
import com.sunyoungeom.booktalk.dto.ReviewDTO;
import com.sunyoungeom.booktalk.dto.ReviewLikesDTO;
import com.sunyoungeom.booktalk.dto.ReviewUpdateDTO;
import com.sunyoungeom.booktalk.service.ReviewService;
import com.sunyoungeom.booktalk.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewApiController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final HttpSession session;

    @PostMapping
    public ResponseEntity<Object> createReview(@Valid @RequestBody ReviewAddDTO reviewAddDTO, BindingResult bindingResult) {
        // 유효성 검사
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return ApiResponseUtil.validatedErrorResponse("유효성 검사 오류", bindingResult);
        }

        Long userId = (Long) session.getAttribute("id");
        String author = (String) session.getAttribute("username");

        // 제목 마지막 공백 제거
        String title = reviewAddDTO.getTitle().replaceAll("\\s+$", "");

        // 현재 시간 yyyy-MM-dd HH:mm:ss 형식으로 변환
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        Review review = Review.builder()
                .userId(userId)
                .title(reviewAddDTO.getTitle())
                .author(author)
                .date(formattedDateTime)
                .content(reviewAddDTO.getContent())
                .build();

        Review result = reviewService.createReview(review, userId, author);

        return ApiResponseUtil.successResponse(HttpStatus.CREATED, "리뷰가 작성되었습니다.", result);
    }

    @GetMapping
    public ResponseEntity<Object> getReviews(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "author", required = false) String author,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            Pageable pageable) {

        Long userId = (Long) session.getAttribute("id");
        String nickname = userService.getNicknameById(userId);

        if (userId == null || (nickname != null && !nickname.equals(author))) {
            Page<ReviewDTO> reviews = reviewService.findReviewsWithLikeStatus(userId, title, author, sortBy, pageable);
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

    @GetMapping("/{id}")
    public ResponseEntity<Object> getReview(
            @PathVariable(name = "id") Long reviewId) {
        Long userId = (Long) session.getAttribute("id");
        Review review = reviewService.existsById(reviewId);

        return ApiResponseUtil.successResponse(HttpStatus.OK, "리뷰 조회에 성공하였습니다.", review);
    }

    @GetMapping("/{id}/like")
    public ResponseEntity<Object> getLikedReviews(
            @PathVariable(name = "id") String id,
            Pageable pageable) {

        Long userId = (Long) session.getAttribute("id");
        Page<ReviewDTO> reviews = reviewService.findLikedReviewsByUserId(userId, pageable);
        if (!reviews.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("reviews", reviews));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "리뷰 검색결과가 없습니다."));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateReview(
            @PathVariable(name = "id") Long reviewId,
            @Valid @RequestBody ReviewUpdateDTO reviewUpdateDTO, BindingResult bindingResult) {
        // 유효성 검사
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return ApiResponseUtil.validatedErrorResponse("유효성 검사 오류", bindingResult);
        }

        Long userId = (Long) session.getAttribute("id");
        reviewService.update(reviewId, userId, reviewUpdateDTO.getContent());

        return ApiResponseUtil.successResponse(HttpStatus.OK, "리뷰가 수정되었습니다.", reviewUpdateDTO);
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
