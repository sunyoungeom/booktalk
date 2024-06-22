package com.sunyoungeom.booktalk.service;

import com.sunyoungeom.booktalk.domain.Review;
import com.sunyoungeom.booktalk.dto.ReviewLikesDTO;
import com.sunyoungeom.booktalk.exception.GlobalExceptionHandler;
import com.sunyoungeom.booktalk.exception.ReviewException;
import com.sunyoungeom.booktalk.exception.ReviewErrorCode;
import com.sunyoungeom.booktalk.exception.common.CommonErrorCode;
import com.sunyoungeom.booktalk.exception.common.CommonException;
import com.sunyoungeom.booktalk.exception.common.ErrorCode;
import com.sunyoungeom.booktalk.repository.ReviewRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository repository;

    public Review createReview(Review review, Long userId, String username) {
        // 중복 리뷰 검증
        validateDuplicateReview(review.getTitle(), userId);
        // 리뷰 저장
        repository.save(review);
        return review;
    }

    private void validateDuplicateReview(String title, Long userId) {
        if (repository.existsByTitleAndUserId(title, userId)) {
            throw new ReviewException(ReviewErrorCode.REVIEW_ALREADY_EXISTS_ERROR.getMessage());
        }
    }

    public List<Review> findAllOrderByDateDesc() {
        return repository.findAllOrderByDateDesc();
    }

    public List<Review> findAllOrderByLikesDesc() {
        return repository.findAllOrderByLikesDesc();
    }

    public List<Review> findByTitleOrderByLikesDesc(String title) {
        return repository.findByTitleOrderByLikesDesc(title);
    }

    public List<Review> findByTitleOrderByDateDesc(String title) {
        return repository.findByTitleOrderByDateDesc(title);
    }

    public List<Review> findByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    public void update(Long reviewId, Long userId, String content) {
        // 리뷰 존재 확인
        Review review = existsById(reviewId);
        // 작성자 일치 확인
        checkAuthorMatch(userId, review);

        review.setContent(content);
        repository.update(review.getId(), content);
    }

    private void checkAuthorMatch(Long userId, Review review) {
        if (review.getUserId() != userId) {
            throw new ReviewException(CommonErrorCode.ACCESS_DENIED_ERROR.getMessage());
        }
    }

    public void deleteReview(Long reviewId, Long userId) {
        // 리뷰 존재 확인
        Review review = existsById(reviewId);
        // 작성자 일치 확인
        checkAuthorMatch(userId, review);

        repository.deleteById(reviewId);
    }

    private Review existsById(Long id) {
        Review review = repository.findById(id)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND_ERROR.getMessage()));
        return review;
    }
}
