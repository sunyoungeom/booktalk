package com.sunyoungeom.booktalk.repository;

import com.sunyoungeom.booktalk.domain.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryMemory implements ReviewRepository {

    private static Map<Long, Review> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public Review save(Review review) {
        review.setId(sequence++);
        review.setDate(LocalDateTime.now().toString());
        store.put(review.getId(), review);
        return review;
    }

    @Override
    public Optional<Review> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public boolean existsByTitleAndUserId(String title, Long userId) {
        for (Review review : store.values()) {
            if (review.getTitle().equals(title) && review.getUserId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Review> findAllOrderByDateDesc() {
        List<Review> reviews = new ArrayList<>(store.values());
        reviews.sort((r1, r2) -> r2.getDate().compareTo(r1.getDate()));
        return reviews;
    }

    @Override
    public List<Review> findAllOrderByLikesDesc() {
        List<Review> reviews = new ArrayList<>(store.values());
        reviews.sort((r1, r2) -> Integer.compare(r2.getLikes(), r1.getLikes()));
        return reviews;
    }

    @Override
    public List<Review> findByTitleOrderByDateDesc(String title) {
        List<Review> reviews =  new ArrayList<>();
        for (Review review : store.values()) {
            if (review.getTitle().equals(title)) {
                reviews.add(review);
            }
        }
        reviews.sort((r1, r2) -> r2.getDate().compareTo(r1.getDate()));
        return reviews;
    }

    @Override
    public List<Review> findByTitleOrderByLikesDesc(String title) {
        List<Review> reviews =  new ArrayList<>();
        for (Review review : store.values()) {
            if (review.getTitle().equals(title)) {
                reviews.add(review);
            }
        }
        reviews.sort((r1, r2) -> Integer.compare(r2.getLikes(), r1.getLikes()));
        return reviews;
    }

    @Override
    public List<Review> findByUserId(Long userId) {
        List<Review> reviews = new ArrayList<>();
        for (Review review : store.values()) {
            if (review.getUserId().equals(userId)) {
                reviews.add(review);
            }
        }
        return reviews;
    }

    @Override
    public void update(Long id, String content) {
        Review review = store.get(id);
        review.setContent(content);
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    public void clearStore() {
        store.clear();
    }
}
