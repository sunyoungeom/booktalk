package com.sunyoungeom.booktalk.service;

import com.sunyoungeom.booktalk.domain.Review;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
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
            throw new ReviewException(ReviewErrorCode.REVIEW_ALREADY_EXISTS_ERROR);
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
        Review review = existsById(reviewId);
        checkAuthorMatch(userId, review, false);

        review.setContent(content);
        reviewRepository.update(review.getId(), content);
    }

    public void deleteReview(Long reviewId, Long userId) {
        Review review = existsById(reviewId);
        checkAuthorMatch(userId, review, false);

        reviewLikesRepository.deleteReview(reviewId);
        reviewRepository.delete(reviewId);
    }

    private void checkAuthorMatch(Long userId, Review review, boolean throwErrorWhenMatch) {
        // 본인이 작성한 리뷰에 좋아요 시도
        if (throwErrorWhenMatch && review.getUserId().equals(userId)) {
            throw new ReviewException(ReviewErrorCode.REVIEW_BY_YOU_ERROR);
            // 권한없는 사용자의 수정, 삭제 시도
        } else if (!throwErrorWhenMatch && !review.getUserId().equals(userId)) {
            throw new ReviewException(CommonErrorCode.ACCESS_DENIED_ERROR);
        }
    }

    public Review existsById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND_ERROR));
        return review;
    }

    public Page<ReviewDTO> findLikedReviewsByUserId(Long userId, Pageable pageable) {
        List<ReviewDTO> reviews = reviewLikesRepository.findLikedReviewsByUserId(userId, pageable);
        int totalElements = reviewLikesRepository.countLikedReviews(userId);
        return new PageImpl<>(reviews, pageable, totalElements);
    }

    public ReviewLikesDTO likeReview(Long reviewId, Long userId) throws InterruptedException {
        validateUser(userId);

        boolean alreadyLiked = reviewLikesRepository.findByUserIdAndReviewId(userId, reviewId); // 좋아요 확인
        int success = 0; // 업데이트 성공 확인

        while (success < 1) {
            try {
                success = likeReviewWithLock(reviewId, userId, alreadyLiked);
            } catch (Exception e) {
                Thread.sleep(50);
            }
        }

        // 사용자 좋아요 업데이트
        reviewLikesRepository.saveOrUpdateLike(userId, reviewId);

        Review updatedReview = existsById(reviewId);
        ReviewLikesDTO reviewLikesDTO = new ReviewLikesDTO();
        reviewLikesDTO.setLiked(!alreadyLiked);
        reviewLikesDTO.setLikes(updatedReview.getLikes());

        return reviewLikesDTO;
    }

    @Transactional
    public int likeReviewWithLock(Long reviewId, Long userId, boolean alreadyLiked) {
        Review review = existsById(reviewId);
        checkAuthorMatch(userId, review, true);
        log.info("리뷰 조회 결과: {}", review.toString());

        Long currentVersion = review.getVersion(); // 버전 확인
        int likeChange = alreadyLiked ? -1 : 1; // 좋아요 상태 토글
        int success = 0; // 업데이트 성공 확인

        // 좋아요 수가 50 이상인 경우 비관적 락 적용
        if (review.getLikes() >= 50) {
            reviewRepository.findByIdForUpdate(reviewId);
        }
        // 좋아요 수가 50 미만인 경우 낙관적 락 적용
        success = reviewRepository.updateLikes(reviewId, likeChange, currentVersion);
        log.info("리뷰 좋아요 수 업데이트 시도: {}", success);

        return success;
    }

    private static void validateUser(Long userId) {
        log.info("Validating userId: {}", userId);
        if (userId == null) {
            log.error("예외발생");
            throw new UserException(CommonErrorCode.ACCESS_DENIED_ERROR);
        }
            log.error("예외발생하지 않음");
    }
}