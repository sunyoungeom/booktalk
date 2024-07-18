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
import java.util.Random;

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

        reviewLikesRepository.deleteReview(reviewId);
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
    public ReviewLikesDTO likeReview(Long reviewId, Long userId) throws InterruptedException {
        ReviewLikesDTO reviewLikesDTO = new ReviewLikesDTO();

        // 사용자 체크
        if (userId == null) {
            throw new UserException(UserErrorCode.USER_NOT_FOUND_ERROR.getMessage());
        }

        int retryCount = 3; // 재시도 횟수
        while (retryCount > 0) {
            try {
                Review review = reviewRepository.findById(reviewId)
                        .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND_ERROR.getMessage()));
                log.info("리뷰 조회 결과: {}", review.toString());

                // 리뷰 작성자 확인
                if (review.getUserId().equals(userId)) {
                    throw new ReviewException(ReviewErrorCode.REVIEW_BY_YOU_ERROR.getMessage());
                }

                // 좋아요 수가 50 이상인 경우 비관적 락 적용
                if (review.getLikes() >= 50) {
                    review = reviewRepository.findByIdForUpdate(reviewId);
                }

                Long currentVersion = review.getVersion(); // 버전 확인
                boolean alreadyLiked = reviewLikesRepository.findByUserIdAndReviewId(userId, reviewId); // 좋아요 확인
                int likeChange = alreadyLiked ? -1 : 1; // 좋아요 상태 토글

                // 리뷰 좋아요 수 업데이트 시도
                int count = reviewRepository.updateLikes(reviewId, likeChange, currentVersion);
                log.info("리뷰 좋아요 수 업데이트 시도: {}", count);

                // 좋아요 성공시
                if (count > 0) {
                    reviewLikesRepository.saveOrUpdateLike(userId, reviewId);

                    Review updatedReview = reviewRepository.findById(reviewId)
                            .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND_ERROR.getMessage()));

                    reviewLikesDTO.setLiked(!alreadyLiked);
                    reviewLikesDTO.setLikes(updatedReview.getLikes());
                    break;
                } else {
                    // 업데이트 실패 시 재시도
                    retryCount--;
                    Thread.sleep(50);
                }
            } catch (Exception e) {
                retryCount--;

                Random random = new Random();
                int randomDelay = random.nextInt(201) + 100;
                Thread.sleep(randomDelay);

                e.printStackTrace();
            }
        }
        return reviewLikesDTO;
    }
}