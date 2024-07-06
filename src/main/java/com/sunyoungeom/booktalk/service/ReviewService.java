package com.sunyoungeom.booktalk.service;

import com.sunyoungeom.booktalk.domain.Review;
import com.sunyoungeom.booktalk.domain.ReviewLikes;
import com.sunyoungeom.booktalk.dto.ReviewDTO;
import com.sunyoungeom.booktalk.dto.ReviewLikesDTO;
import com.sunyoungeom.booktalk.exception.ReviewException;
import com.sunyoungeom.booktalk.exception.ReviewErrorCode;
import com.sunyoungeom.booktalk.exception.UserErrorCode;
import com.sunyoungeom.booktalk.exception.UserException;
import com.sunyoungeom.booktalk.exception.common.CommonErrorCode;
import com.sunyoungeom.booktalk.facade.ReviewFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewFacade reviewFacade;

    public Page<ReviewDTO> findReviewsWithLikeStatus(Long userId, String title, String sortBy, Pageable pageable) {
        List<ReviewDTO> reviews = new ArrayList<>();

        // 리뷰 검색
        int totalElements = 0;
        if (title != null) {
            totalElements = countReviewsByTitle(title);
            if ("popularity".equalsIgnoreCase(sortBy)) {
                reviews = findByTitleOrderByLikesDesc(userId, title, pageable);
            } else {
                reviews = findByTitleOrderByDateDesc(userId, title, pageable);
            }
        } else {
            totalElements = countReviews();
            if ("popularity".equalsIgnoreCase(sortBy)) {
                reviews = findAllOrderByLikesDesc(userId, pageable);
            } else {
               reviews = findAllOrderByDateDesc(userId, pageable);
            }
        }
        return new PageImpl<>(reviews, pageable, totalElements);
    }

    public Review createReview(Review review, Long userId, String username) {
        // 중복 리뷰 검증
        Optional<Review> result = validateDuplicateReview(review.getTitle(), userId);
        if (result.isEmpty()) {
            // 리뷰 저장
            reviewFacade.saveReview(review);
            return review;
        } else {
            // 중복 리뷰인 경우 예외
            throw new ReviewException(ReviewErrorCode.REVIEW_ALREADY_EXISTS_ERROR.getMessage());
        }
    }

    public Optional<Review> validateDuplicateReview(String title, Long userId) {
        Optional<Review> review = reviewFacade.existsReviewByTitleAndUserId(title, userId);
        return review;
    }

    public List<ReviewDTO> findAllOrderByDateDesc(Long userId, Pageable pageable) {
        return reviewFacade.findAllReviewsOrderByDateDesc(userId, pageable);
    }

    public List<ReviewDTO> findAllOrderByLikesDesc(Long userId, Pageable pageable) {
        return reviewFacade.findAllReviewsOrderByLikesDesc(userId, pageable);
    }

    public List<ReviewDTO> findByTitleOrderByLikesDesc(Long userId, String title, Pageable pageable) {
        return reviewFacade.findReviewsByTitleOrderByLikesDesc(userId, title, pageable);
    }

    public List<ReviewDTO> findByTitleOrderByDateDesc(Long userId, String title, Pageable pageable) {
        return reviewFacade.findReviewsByTitleOrderByDateDesc(userId, title, pageable);
    }

    public int countReviews() {
        return reviewFacade.countReviews();
    }

    public int countReviewsByTitle(String title) {
        return reviewFacade.countReviewsByTitle(title);
    }

    public Page<ReviewDTO> findByUserId(Long userId, Pageable pageable) {
        List<ReviewDTO> reviews = reviewFacade.findReviewsByUserId(userId, pageable);
        int total = countReviewsByUserId(userId);
        return new PageImpl<>(reviews, pageable, total);

    }

    public int countReviewsByUserId(Long userId) {
        return reviewFacade.countReviewsByUserId(userId);
    }

    public void update(Long reviewId, Long userId, String content) {
        // 리뷰 존재 확인
        Review review = existsById(reviewId);
        // 작성자 일치 확인
        checkAuthorMatch(userId, review);

        review.setContent(content);
        reviewFacade.updateReviewContent(review.getId(), content);
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

        reviewFacade.deleteReviewById(reviewId);
    }

    public Review existsById(Long id) {
        Review review = reviewFacade.findReviewById(id)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND_ERROR.getMessage()));
        return review;
    }

    public List<ReviewLikes> findAllReviewLikes() {
        return reviewFacade.findAllReviewLikes();
    }

    @Transactional
    public ReviewLikesDTO likeReview(Long reviewId, Long userId) {
        Review review = reviewFacade.findReviewById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND_ERROR.getMessage()));
        ReviewLikesDTO reviewLikesDTO = new ReviewLikesDTO();
        if (userId == null) {
            throw new UserException(UserErrorCode.USER_NOT_FOUND_ERROR.getMessage());
        }
        if (review.getUserId().equals(userId)) {
            throw new ReviewException(ReviewErrorCode.REVIEW_BY_YOU_ERROR.getMessage());
        }
        boolean alreadyLiked = reviewFacade.findByUserIdAndReviewId(userId, reviewId);
        if (alreadyLiked) {
            reviewLikesDTO.setLiked(false);
            reviewLikesDTO.setLikes(review.getLikes() - 1);
            reviewFacade.decreaseLikes(reviewId);
            reviewFacade.deleteReviewLikes(userId, reviewId);
        } else {
            reviewLikesDTO.setLiked(true);
            reviewLikesDTO.setLikes(review.getLikes() + 1);
            reviewFacade.increaseLikes(reviewId);
            ReviewLikes reviewLikes = new ReviewLikes(userId, reviewId);
            reviewFacade.saveReviewLikes(reviewLikes);
        }
        return reviewLikesDTO;
    }
}
