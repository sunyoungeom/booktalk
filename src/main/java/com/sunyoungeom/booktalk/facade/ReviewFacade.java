package com.sunyoungeom.booktalk.facade;


import com.sunyoungeom.booktalk.domain.Review;
import com.sunyoungeom.booktalk.domain.ReviewLikes;
import com.sunyoungeom.booktalk.dto.ReviewDTO;
import com.sunyoungeom.booktalk.repository.ReviewLikesRepository;
import com.sunyoungeom.booktalk.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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

    public List<ReviewDTO> findAllReviewsOrderByDateDesc(Long userid, Pageable pageable) {
        return reviewRepository.findAllOrderByDateDesc(userid, pageable);
    }

    public List<ReviewDTO> findAllReviewsOrderByLikesDesc(Long userId, Pageable pageable) {
        return reviewRepository.findAllOrderByLikesDesc(userId, pageable);
    }

    public List<ReviewDTO> findReviewsByTitleOrderByDateDesc(Long userId, String title, Pageable pageable) {
        return reviewRepository.findByTitleOrderByDateDesc(userId, title, pageable);
    }

    public List<ReviewDTO> findReviewsByTitleOrderByLikesDesc(Long userId, String title, Pageable pageable) {
        return reviewRepository.findByTitleOrderByLikesDesc(userId, title, pageable);
    }

    public int countReviews() {
        return reviewRepository.countReviews();
    }

    public int countReviewsByTitle(String title) {
        return reviewRepository.countReviewsByTitle(title);
    }

    public int countReviewsByUserId(Long userId) {
        return reviewRepository.countReviewsByUserId(userId);
    }

    public List<ReviewDTO> findReviewsByUserId(Long userId, Pageable pageable) {
        return reviewRepository.findByUserId(userId, pageable);
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

