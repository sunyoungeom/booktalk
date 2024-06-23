package com.sunyoungeom.booktalk.facade;


import com.sunyoungeom.booktalk.domain.Review;
import com.sunyoungeom.booktalk.domain.ReviewLikes;
import com.sunyoungeom.booktalk.repository.ReviewLikesRepository;
import com.sunyoungeom.booktalk.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
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

    public boolean existsReviewByTitleAndUserId(String title, Long userId) {
        return reviewRepository.existsByTitleAndUserId(title, userId);
    }

    public List<Review> findAllReviewsOrderByDateDesc() {
        return reviewRepository.findAllOrderByDateDesc();
    }

    public List<Review> findAllReviewsOrderByLikesDesc() {
        return reviewRepository.findAllOrderByLikesDesc();
    }

    public List<Review> findReviewsByTitleOrderByDateDesc(String title) {
        return reviewRepository.findByTitleOrderByDateDesc(title);
    }

    public List<Review> findReviewsByTitleOrderByLikesDesc(String title) {
        return reviewRepository.findByTitleOrderByLikesDesc(title);
    }

    public List<Review> findReviewsByUserId(Long userId) {
        return reviewRepository.findByUserId(userId);
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

    public void clearStore() {
        reviewRepository.clearStroe();
    }
}

