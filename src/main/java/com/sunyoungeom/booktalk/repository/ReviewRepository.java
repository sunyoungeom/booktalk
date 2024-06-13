package com.sunyoungeom.booktalk.repository;

import com.sunyoungeom.booktalk.domain.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {

    Review save(Review review);
    Optional<Review> findById(Long id);
    Optional<Review> findByTitleAndAuthor(String title, String author);
    List<Review> findAll();
    List<Review> findAllSortedBydate();
    List<Review> findAllSortedByLikes();
    List<Review> findByTitle(String title);
    List<Review> findByTitleSortedByLikes(String title);
    List<Review> findByTitleSortedByDate(String title);
    List<Review> findByAuthor(String author);
    Review update(Long id, String content);
    void deleteById(Long id);

}
