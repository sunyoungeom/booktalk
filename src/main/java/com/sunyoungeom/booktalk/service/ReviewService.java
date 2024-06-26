package com.sunyoungeom.booktalk.service;

import com.sunyoungeom.booktalk.domain.Review;
import com.sunyoungeom.booktalk.domain.ReviewLikes;
import com.sunyoungeom.booktalk.dto.ReviewLikesDTO;
import com.sunyoungeom.booktalk.exception.ReviewException;
import com.sunyoungeom.booktalk.exception.ReviewErrorCode;
import com.sunyoungeom.booktalk.exception.UserErrorCode;
import com.sunyoungeom.booktalk.exception.UserException;
import com.sunyoungeom.booktalk.exception.common.CommonErrorCode;
import com.sunyoungeom.booktalk.facade.ReviewFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewFacade reviewFacade;

    public Review createReview(Review review, Long userId, String username) {
        // 중복 리뷰 검증
        validateDuplicateReview(review.getTitle(), userId);
        // 리뷰 저장
        reviewFacade.saveReview(review);
        return review;
    }

    private void validateDuplicateReview(String title, Long userId) {
        if (reviewFacade.existsReviewByTitleAndUserId(title, userId)) {
            throw new ReviewException(ReviewErrorCode.REVIEW_ALREADY_EXISTS_ERROR.getMessage());
        }
    }

    public List<Review> findAllOrderByDateDesc() {
        return reviewFacade.findAllReviewsOrderByDateDesc();
    }

    public List<Review> findAllOrderByLikesDesc() {
        return reviewFacade.findAllReviewsOrderByLikesDesc();
    }

    public List<Review> findByTitleOrderByLikesDesc(String title) {
        return reviewFacade.findReviewsByTitleOrderByLikesDesc(title);
    }

    public List<Review> findByTitleOrderByDateDesc(String title) {
        return reviewFacade.findReviewsByTitleOrderByDateDesc(title);
    }

    public List<Review> findByUserId(Long userId) {
        return reviewFacade.findReviewsByUserId(userId);
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

    private Review existsById(Long id) {
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
