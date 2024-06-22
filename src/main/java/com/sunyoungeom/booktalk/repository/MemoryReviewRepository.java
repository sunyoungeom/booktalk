package com.sunyoungeom.booktalk.repository;

import com.sunyoungeom.booktalk.domain.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MemoryReviewRepository implements ReviewRepository {

    private static Map<Long, Review> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public Review save(Review review) {
        review.setId(sequence++);
        store.put(review.getId(), review);
        return review;
    }

    @Override
    public Optional<Review> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Review> findByTitleAndAuthor(String title, String author) {
        return store.values().stream()
                .filter(review -> review.getTitle().equals(title) && review.getAuthor().equals(author))
                .findAny();
    }

    @Override
    public List<Review> findByTitle(String title) {
        return store.values().stream().filter(review -> review.getTitle().equals(title)).collect(Collectors.toList());
    }

    @Override
    public List<Review> findByTitleSortedByLikes(String title) {
        List<Review> reviews = findByTitle(title);
        reviews.sort((r1, r2) -> Integer.compare(r2.getLikes(), r1.getLikes()));
        return reviews;
    }

    @Override
    public List<Review> findByTitleSortedByDate(String title) {
        List<Review> reviews = findByTitle(title);
        reviews.sort((r1, r2) -> r2.getDate().compareTo(r1.getDate()));
        return reviews;
    }

    @Override
    public List<Review> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Review> findAllSortedBydate() {
        List<Review> reviews = findAll();
        reviews.sort((r1, r2) -> r2.getDate().compareTo(r1.getDate()));
        return reviews;
    }

    @Override
    public List<Review> findAllSortedByLikes() {
        List<Review> reviews = findAll();
        reviews.sort((r1, r2) -> Integer.compare(r2.getLikes(), r1.getLikes()));
        return reviews;
    }

    @Override
    public Review update(Long id, String content) {
        Review review = findById(id).orElseThrow(() ->
                new IllegalStateException("해당 ID의 리뷰를 찾을 수 없습니다"));
        review.setContent(content);
        return review;
    }

    @Override
    public List<Review> findByAuthor(String author) {
        return store.values().stream().filter(review -> review.getAuthor().equals(author)).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    public void clearStore() {
        store.clear();
    }
}
