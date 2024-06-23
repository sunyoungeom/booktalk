package com.sunyoungeom.booktalk.repository;

import com.sunyoungeom.booktalk.domain.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {

    Review save(Review review);
    Optional<Review> findById(Long id);
    boolean existsByTitleAndUserId(String title, Long userId);
    List<Review> findAllOrderByDateDesc();
    List<Review> findAllOrderByLikesDesc();
    List<Review> findByTitleOrderByDateDesc(String title);
    List<Review> findByTitleOrderByLikesDesc(String title);
    List<Review> findByUserId(Long userId);
    void update(Long id, String content);
    void delete(Long id);

    void clearStroe();
}
