package com.sunyoungeom.booktalk.repository;

import com.sunyoungeom.booktalk.domain.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {

    Review save(Review review);
    Optional<Review> findById(Long id);
    Optional<Review> existsByTitleAndUserId(String title, Long userId);
    List<Review> findAllOrderByDateDesc();
    List<Review> findAllOrderByLikesDesc();
    List<Review> findByTitleOrderByDateDesc(String title);
    List<Review> findByTitleOrderByLikesDesc(String title);
    List<Review> findByUserId(Long userId);
    void increaseLikes(Long id);
    void decreaseLikes(Long id);
    void update(Long id, String content);
    void delete(Long id);

}
