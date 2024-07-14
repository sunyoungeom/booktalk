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
import com.sunyoungeom.booktalk.repository.ReviewLikesRepository;
import com.sunyoungeom.booktalk.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewLikesRepository reviewLikesRepository;

    public Page<ReviewDTO> findReviewsWithLikeStatus(Long userId, String title, String author, String sortBy, Pageable pageable) {
        List<ReviewDTO> reviews = new ArrayList<>();

        // 리뷰 검색
        int totalElements = 0;
        if (title != null) {
            totalElements = countReviewsByTitle(title);
            if ("popularity".equalsIgnoreCase(sortBy)) {
                reviews = findByTitleOrderByLikesDesc(userId, title, author, pageable);
            } else {
                reviews = findByTitleOrderByDateDesc(userId, title, author, pageable);
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
        Integer result = validateDuplicateReview(userId, review.getTitle());
        if (result == null) {
            // 리뷰 저장
            reviewRepository.save(review);
            return review;
        } else {
            // 중복 리뷰인 경우 예외
            throw new ReviewException(ReviewErrorCode.REVIEW_ALREADY_EXISTS_ERROR.getMessage());
        }
    }

    public Integer validateDuplicateReview(Long userId, String title) {
        return reviewRepository.existsByTitleAndUserId(userId, title);
    }

    public List<ReviewDTO> findAllOrderByDateDesc(Long userId, Pageable pageable) {
        return reviewRepository.findAllOrderByDateDesc(userId, pageable);
    }

    public List<ReviewDTO> findAllOrderByLikesDesc(Long userId, Pageable pageable) {
        return reviewRepository.findAllOrderByLikesDesc(userId, pageable);
    }

    public List<ReviewDTO> findByTitleOrderByDateDesc(Long userId, String title, String author, Pageable pageable) {
        return reviewRepository.findByTitleOrderByDateDesc(userId, title, author, pageable);
    }

    public List<ReviewDTO> findByTitleOrderByLikesDesc(Long userId, String title, String author, Pageable pageable) {
        return reviewRepository.findByTitleOrderByLikesDesc(userId, title, author, pageable);
    }

    public int countReviews() {
        return reviewRepository.countReviews();
    }

    public int countReviewsByTitle(String title) {
        return reviewRepository.countReviewsByTitle(title);
    }

    public Page<ReviewDTO> findByUserId(Long userId, Pageable pageable) {
        List<ReviewDTO> reviews = reviewRepository.findByUserId(userId, pageable);
        int total = countReviewsByUserId(userId);
        return new PageImpl<>(reviews, pageable, total);

    }

    public int countReviewsByUserId(Long userId) {
        return reviewRepository.countReviewsByUserId(userId);
    }

    public void update(Long reviewId, Long userId, String content) {
        // 리뷰 존재 확인
        Review review = existsById(reviewId);
        // 작성자 일치 확인
        checkAuthorMatch(userId, review);

        review.setContent(content);
        reviewRepository.update(review.getId(), content);
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

        reviewRepository.delete(reviewId);
    }

    public Review existsById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND_ERROR.getMessage()));
        return review;
    }

    public Page<ReviewDTO> findLikedReviewsByUserId(Long userId, Pageable pageable) {
        List<ReviewDTO> reviews = reviewLikesRepository.findLikedReviewsByUserId(userId, pageable);
        int totalElements = reviewLikesRepository.countLikedReviews(userId);
        return new PageImpl<>(reviews, pageable, totalElements);
    }

    @Transactional
    public ReviewLikesDTO likeReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND_ERROR.getMessage()));
        ReviewLikesDTO reviewLikesDTO = new ReviewLikesDTO();
        if (userId == null) {
            throw new UserException(UserErrorCode.USER_NOT_FOUND_ERROR.getMessage());
        }
        if (review.getUserId().equals(userId)) {
            throw new ReviewException(ReviewErrorCode.REVIEW_BY_YOU_ERROR.getMessage());
        }
        boolean alreadyLiked = reviewLikesRepository.findByUserIdAndReviewId(userId, reviewId);
        if (alreadyLiked) {
            reviewLikesDTO.setLiked(false);
            reviewLikesDTO.setLikes(review.getLikes() - 1);
            reviewRepository.decreaseLikes(reviewId);
            reviewLikesRepository.delete(userId, reviewId);
        } else {
            reviewLikesDTO.setLiked(true);
            reviewLikesDTO.setLikes(review.getLikes() + 1);
            reviewRepository.increaseLikes(reviewId);
            ReviewLikes reviewLikes = new ReviewLikes(userId, reviewId);
            reviewLikesRepository.save(reviewLikes);
        }
        return reviewLikesDTO;
    }
}
