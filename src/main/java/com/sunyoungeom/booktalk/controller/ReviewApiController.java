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
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewApiController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Object> createReview(
            @SessionAttribute(name = "userId") Long userId,
            @SessionAttribute(name = "username") String author,
            @Valid @RequestBody ReviewAddDTO reviewAddDTO, BindingResult bindingResult) {
        // 유효성 검사
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return ApiResponseUtil.validatedErrorResponse("유효성 검사 오류", bindingResult);
        }

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
            @SessionAttribute(name = "userId", required = false) Long userId,
            @SessionAttribute(name = "username", required = false) String username,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "author", required = false) String author,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "liked", required = false) String liked,
            Pageable pageable) {
        // 좋아요 한 리뷰 조회
        if (userId != null && liked != null && liked.equals("true")) {
            Page<ReviewDTO> reviews = reviewService.findLikedReviewsByUserId(userId, pageable);
            if (!reviews.get().collect(Collectors.toList()).isEmpty()) {
                return ApiResponseUtil.successResponse(HttpStatus.OK, "좋아요한 리뷰 조회에 성공했습니다.", reviews);
            } else {
                return ApiResponseUtil.failResponse(HttpStatus.NOT_FOUND, "좋아요 한 리뷰가 없습니다.");
            }
        }
        // 비회원 조회 || 작성자 조건 조회
        else if (userId == null || (userId != null && username != null && !username.equals(author))) {
            Page<ReviewDTO> reviews = reviewService.findReviewsWithLikeStatus(userId, title, author, sortBy, pageable);
            if (!reviews.get().collect(Collectors.toList()).isEmpty()) {
                return ApiResponseUtil.successResponse(HttpStatus.OK, "리뷰 조회에 성공했습니다.", reviews);
            } else {
                return ApiResponseUtil.failResponse(HttpStatus.NOT_FOUND, "리뷰 검색결과가 없습니다.");
            }
            // 본인 작성 리뷰 조회
        } else {
            Page<ReviewDTO> reviews = reviewService.findByUserId(userId, pageable);
            if (!reviews.isEmpty()) {
                return ApiResponseUtil.successResponse(HttpStatus.OK, "본인이 작성한 리뷰 조회에 성공했습니다.", reviews);
            } else {
                return ApiResponseUtil.failResponse(HttpStatus.NOT_FOUND, "작성한 리뷰가 없습니다.");
            }
        }
    }

    // 수정 페이지에서 리뷰 조회
    @GetMapping("/{id}")
    public ResponseEntity<Object> getReview(
            @SessionAttribute(name = "userId") Long userId,
            @PathVariable(name = "id") Long reviewId) {
        Review review = reviewService.existsById(reviewId);
        if (userId != review.getUserId()) {
            return ApiResponseUtil.failResponse(HttpStatus.FORBIDDEN, "작성자만 조회가 가능합니다.");
        }
        return ApiResponseUtil.successResponse(HttpStatus.OK, "리뷰 조회에 성공하였습니다.", review);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateReview(
            @SessionAttribute(name = "userId") Long userId,
            @PathVariable(name = "id") Long reviewId,
            @Valid @RequestBody ReviewUpdateDTO reviewUpdateDTO, BindingResult bindingResult) {
        // 유효성 검사
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return ApiResponseUtil.validatedErrorResponse("유효성 검사 오류", bindingResult);
        }

        reviewService.update(reviewId, userId, reviewUpdateDTO.getContent());

        return ApiResponseUtil.successResponse(HttpStatus.OK, "리뷰가 수정되었습니다.", reviewUpdateDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteReview(
            @SessionAttribute(name = "userId") Long userId,
            @PathVariable(name = "id") Long reviewId) {
        reviewService.deleteReview(reviewId, userId);
        return ApiResponseUtil.successResponse(HttpStatus.NO_CONTENT, "리뷰가 성공적으로 삭제되었습니다.", "");
    }

    @PostMapping("/{id}/likes")
    public ResponseEntity<Object> likeReview(
            @SessionAttribute(name = "userId") Long userId,
            @PathVariable(name = "id") Long reviewId) {
        ReviewLikesDTO reviewLikesDTO = reviewService.likeReview(reviewId, userId);
        return ApiResponseUtil.successResponse(HttpStatus.OK, "좋아요가 반영되었습니다.", reviewLikesDTO);
    }
}
