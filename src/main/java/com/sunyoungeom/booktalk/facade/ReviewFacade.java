package com.sunyoungeom.booktalk.facade;


import com.sunyoungeom.booktalk.domain.Review;
import com.sunyoungeom.booktalk.domain.ReviewLikes;
import com.sunyoungeom.booktalk.dto.ReviewDTO;
import com.sunyoungeom.booktalk.repository.ReviewLikesRepository;
import com.sunyoungeom.booktalk.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReviewFacade {
    private final ReviewRepository reviewRepository;
    private final ReviewLikesRepository reviewLikesRepository;

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    public Optional<Review> findReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    public Optional<Review> existsReviewByTitleAndUserId(String title, Long userId) {
        return reviewRepository.existsByTitleAndUserId(title, userId);
    }

    public List<ReviewDTO> findAllReviewsOrderByDateDesc(Long userid, RowBounds pageable) {
        return reviewRepository.findAllOrderByDateDesc(userid, pageable);
    }

    public List<ReviewDTO> findAllReviewsOrderByLikesDesc(Long userId, RowBounds pageable) {
        return reviewRepository.findAllOrderByLikesDesc(userId, pageable);
    }

    public List<ReviewDTO> findReviewsByTitleOrderByDateDesc(Long userId, String title, RowBounds pageable) {
        return reviewRepository.findByTitleOrderByDateDesc(userId, title, pageable);
    }

    public List<ReviewDTO> findReviewsByTitleOrderByLikesDesc(Long userId, String title, RowBounds pageable) {
        return reviewRepository.findByTitleOrderByLikesDesc(userId, title, pageable);
    }

    public int countReviews(Long userId) {
        return reviewRepository.countReviews(userId);
    }

    public int countReviewsByTitle(Long userId, String title) {
        return reviewRepository.countReviewsByTitle(userId, title);
    }

    public List<Review> findReviewsByUserId(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    public void increaseLikes(Long id) {
        reviewRepository.increaseLikes(id);
    }

    public void decreaseLikes(Long id) {
        reviewRepository.decreaseLikes(id);
    }

    public void updateReviewContent(Long id, String content) {
        reviewRepository.update(id, content);
    }

    public void deleteReviewById(Long id) {
        reviewRepository.delete(id);
    }

    public ReviewLikes saveReviewLikes(ReviewLikes reviewLikes) {
        return reviewLikesRepository.save(reviewLikes);
    }

    public Optional<ReviewLikes> findReviewLikesById(Long id) {
        return reviewLikesRepository.findById(id);
    }

    public List<ReviewLikes> findAllReviewLikes() {
        return reviewLikesRepository.findAll();
    }

    public List<ReviewLikes> findReviewLikesByUserId(Long userId) {
        return reviewLikesRepository.findReviewLikesByUserId(userId);
    }

    public boolean findByUserIdAndReviewId(Long userId, Long reviewId) {
        return reviewLikesRepository.findByUserIdAndReviewId(userId, reviewId);
    }

    public boolean findByUserId(Long userId) {
        return reviewLikesRepository.findByUserId(userId);
    }

    public void deleteReviewLikes(Long userId, Long reviewId) {
        reviewLikesRepository.delete(userId, reviewId);
    }

}

