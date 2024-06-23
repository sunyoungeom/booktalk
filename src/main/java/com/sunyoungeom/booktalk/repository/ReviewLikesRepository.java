package com.sunyoungeom.booktalk.repository;

import com.sunyoungeom.booktalk.domain.ReviewLikes;

import java.util.List;
import java.util.Optional;

public interface ReviewLikesRepository {

    ReviewLikes save(ReviewLikes reviewLikes);
    List<ReviewLikes> findAll();
    Optional<ReviewLikes> findById(Long id);
    List<ReviewLikes> findReviewLikesByUserId(Long userId);
    boolean findByUserIdAndReviewId(Long userId, Long reviewId);
    boolean findByUserId(Long userId);
    void delete(Long userId, Long reviewId);

}
