package com.sunyoungeom.booktalk.repository;

import com.sunyoungeom.booktalk.domain.Review;
import com.sunyoungeom.booktalk.dto.ReviewDTO;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {

    Review save(Review review);
    Optional<Review> findById(Long id);
    Optional<Review> existsByTitleAndUserId(String title, Long userId);
    List<ReviewDTO> findAllOrderByDateDesc(Long userId);
    List<ReviewDTO> findAllOrderByLikesDesc(Long userId);
    List<ReviewDTO> findByTitleOrderByDateDesc(Long userId, String title);
    List<ReviewDTO> findByTitleOrderByLikesDesc(Long userId, String title);
    List<Review> findByUserId(Long userId);
    void increaseLikes(Long id);
    void decreaseLikes(Long id);
    void update(Long id, String content);
    void delete(Long id);

}
