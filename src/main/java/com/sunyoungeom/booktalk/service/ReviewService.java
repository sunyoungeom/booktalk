package com.sunyoungeom.booktalk.service;

import com.sunyoungeom.booktalk.domain.Review;
import com.sunyoungeom.booktalk.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository repository;

    @Autowired
    public ReviewService(ReviewRepository repository) {
        this.repository = repository;
    }

    public Review createReview(Review review) {
        // 중복 리뷰 검증(사용자는 하나의 책에 대해 하나의 리뷰만 가능)
        validateDuplicateReview(review);
        // 리뷰 저장
        repository.save(review);
        return review;
    }

    private void validateDuplicateReview(Review review) {
        repository.findByTitleAndAuthor(review.getTitle(), review.getAuthor())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 작성한 리뷰입니다.");
                });
    }

    public List<Review> findAll() {
        return repository.findAll();
    }

    public List<Review> findAllSortedBydate() {
        return repository.findAllSortedBydate();
    }

    public List<Review> findAllSortedByLikes() {
        return repository.findAllSortedByLikes();
    }

    public List<Review> findByTitleSortedByLikes(String title) {
        return repository.findByTitleSortedByLikes(title);
    }

    public List<Review> findByTitleSortedByDate(String title) {
        return repository.findByTitleSortedByDate(title);
    }

    public List<Review> findByAuthor(String author) {
        return repository.findByAuthor(author);
    }

    public void updateReview(Long id, String content) {
        // 리뷰 존재 확인
        Review review = existsById(id);

        review.setContent(content);
        repository.update(review.getId(), content);
    }

    public void deleteReview(Long id) {
        // 리뷰 존재 확인
        Review review = existsById(id);

        repository.deleteById(id);
    }

    private Review existsById(Long id) {
        Review review = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("해당 ID의 리뷰를 찾을 수 없습니다."));
        return review;
    }
}
