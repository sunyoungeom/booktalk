package com.sunyoungeom.booktalk.controller;

import com.sunyoungeom.booktalk.common.ApiResponseUtil;
import com.sunyoungeom.booktalk.common.CustomApiResponse;
import com.sunyoungeom.booktalk.domain.Review;
import com.sunyoungeom.booktalk.dto.ReviewAddDTO;
import com.sunyoungeom.booktalk.dto.ReviewDTO;
import com.sunyoungeom.booktalk.dto.ReviewLikesDTO;
import com.sunyoungeom.booktalk.dto.ReviewUpdateDTO;
import com.sunyoungeom.booktalk.exception.ReviewException;
import com.sunyoungeom.booktalk.exception.UserException;
import com.sunyoungeom.booktalk.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewApiController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "리뷰 작성", description = "새로운 리뷰를 등록하는 API입니다. " +
            "입력된 리뷰 정보에 대해 유효성 검사를 수행하고, 사용자가 동일한 도서의 리뷰를 작성한 적 있는지 확인합니다.")
    @ApiResponse(responseCode = "400", description = "유효성 검사에서 오류가 발생하였습니다.")
    @ApiResponse(responseCode = "409", description = "해당 도서에 대한 리뷰를 이미 작성하였습니다.")
    @ApiResponse(responseCode = "201", description = "리뷰 작성에 성공하였습니다.")
    public ResponseEntity<CustomApiResponse> createReview(@Valid @RequestBody ReviewAddDTO reviewAddDTO,
                                                          BindingResult bindingResult,
                                                          HttpServletRequest request) {
        // 유효성 검사
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return ApiResponseUtil.validatedErrorResponse("유효성 검사 오류", bindingResult);
        }

        Long userId = (Long) request.getSession().getAttribute("userId");
        String author = (String) request.getSession().getAttribute("username");

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

        Review result = null;
        try {
            result = reviewService.createReview(review, userId, author);
        } catch (ReviewException e) {
            return ApiResponseUtil.errorResponse(e.getHttpStatus(), e.getMessage());
        }

        return ApiResponseUtil.successResponse(HttpStatus.CREATED, "리뷰가 작성되었습니다.", result);
    }

    @GetMapping
    @Operation(summary = "리뷰 목록 조회", description = "리뷰를 조회하는 API입니다.")
    @ApiResponse(responseCode = "404", description = "리뷰 검색 결과가 존재하지 않습니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 목록 조회에 성공하였습니다.")
    public ResponseEntity<CustomApiResponse> getReviews(@RequestParam(name = "title", required = false) String title,
                                                        @RequestParam(name = "author", required = false) String author,
                                                        @RequestParam(name = "sortBy", required = false) String sortBy,
                                                        @RequestParam(name = "liked", required = false) String liked,
                                                        Pageable pageable, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String username = (String) request.getSession().getAttribute("username");
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

    @GetMapping("/{id}")
    @Operation(summary = "리뷰 조회", description = "리뷰 수정을 위해 작성한 리뷰를 조회하는 API입니다.")
    @ApiResponse(responseCode = "404", description = "해당 ID의 리뷰가 존재하지 않습니다.")
    @ApiResponse(responseCode = "403", description = "권한이 없는 사용자입니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 조회에 성공하였습니다.")
    public ResponseEntity<CustomApiResponse> getReview(@PathVariable(name = "id") Long reviewId,
                                                       HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");

        Review review = null;
        try {
            review = reviewService.existsById(reviewId);
        } catch (ReviewException e) {
            return ApiResponseUtil.errorResponse(e.getHttpStatus(), e.getMessage());
        }

        if (userId != review.getUserId()) {
            return ApiResponseUtil.failResponse(HttpStatus.FORBIDDEN, "작성자만 조회가 가능합니다.");
        }

        return ApiResponseUtil.successResponse(HttpStatus.OK, "리뷰 조회에 성공하였습니다.", review);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "리뷰 수정", description = "리뷰의 내용을 수정하는 API입니다. 입력된 리뷰 내용에 대해 유효성 검사를 수행합니다.")
    @ApiResponse(responseCode = "404", description = "해당 ID의 리뷰가 존재하지 않습니다.")
    @ApiResponse(responseCode = "400", description = "유효성 검사에서 오류가 발생하였습니다.")
    @ApiResponse(responseCode = "403", description = "권한이 없는 사용자입니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 수정에 성공하였습니다.")
    public ResponseEntity<CustomApiResponse> updateReview(@PathVariable(name = "id") Long reviewId,
                                                          @Valid @RequestBody ReviewUpdateDTO reviewUpdateDTO,
                                                          BindingResult bindingResult, HttpServletRequest request) {
        // 유효성 검사
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return ApiResponseUtil.validatedErrorResponse("유효성 검사 오류", bindingResult);
        }

        Long userId = (Long) request.getSession().getAttribute("userId");
        try {
            reviewService.update(reviewId, userId, reviewUpdateDTO.getContent());
        } catch (ReviewException e) {
            return ApiResponseUtil.errorResponse(e.getHttpStatus(), e.getMessage());
        }

        return ApiResponseUtil.successResponse(HttpStatus.OK, "리뷰가 수정되었습니다.", reviewUpdateDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제하는 API입니다.")
    @ApiResponse(responseCode = "404", description = "해당 ID의 리뷰가 존재하지 않습니다.")
    @ApiResponse(responseCode = "403", description = "권한이 없는 사용자입니다.")
    @ApiResponse(responseCode = "204", description = "리뷰 삭제 성공하였습니다.")
    public ResponseEntity<CustomApiResponse> deleteReview(@PathVariable(name = "id") Long reviewId,
                                                          HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        try {
            reviewService.deleteReview(reviewId, userId);
        } catch (ReviewException e) {
            return ApiResponseUtil.errorResponse(e.getHttpStatus(), e.getMessage());
        }
        return ApiResponseUtil.successResponse(HttpStatus.NO_CONTENT, "리뷰가 성공적으로 삭제되었습니다.", "");
    }

    @PostMapping("/{id}/likes")
    @Operation(summary = "리뷰 좋아요", description = "리뷰에 좋아요를 반영하는 API입니다.")
    @ApiResponse(responseCode = "404", description = "해당 ID의 리뷰가 존재하지 않습니다.")
    @ApiResponse(responseCode = "403", description = "비회원은 좋아요를 누를 수 없습니다.")
    @ApiResponse(responseCode = "409", description = "본인이 쓴 리뷰에는 좋아요를 누를 수 없습니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 좋아요에 성공하였습니다.")
    public ResponseEntity<CustomApiResponse> likeReview(@PathVariable(name = "id") Long reviewId,
                                                        HttpServletRequest request) throws InterruptedException {
        Long userId = (Long) request.getSession().getAttribute("userId");
        ReviewLikesDTO reviewLikesDTO = null;
        try {
            reviewLikesDTO = reviewService.likeReview(reviewId, userId);
        } catch (UserException e) {
            return ApiResponseUtil.errorResponse(e.getHttpStatus(), e.getMessage());
        } catch (ReviewException e) {
            return ApiResponseUtil.errorResponse(e.getHttpStatus(), e.getMessage());
        }
        return ApiResponseUtil.successResponse(HttpStatus.OK, "좋아요가 반영되었습니다.", reviewLikesDTO);
    }
}
