package com.sunyoungeom.booktalk.service;

import com.sunyoungeom.booktalk.domain.Review;
import com.sunyoungeom.booktalk.exception.ReviewException;
import com.sunyoungeom.booktalk.exception.ReviewErrorCode;
import com.sunyoungeom.booktalk.repository.ReviewRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    
    private final ReviewRepository repository;
    private final HttpSession session;

    public Review createReview(Review review) {
        // 중복 리뷰 검증
        validateDuplicateReview(review);
        session.setAttribute("currentUser", "작성자");

        // 리뷰 저장
        repository.save(review);
        return review;
    }

    private void validateDuplicateReview(Review review) {
        repository.findByTitleAndAuthor(review.getTitle(), review.getAuthor())
                .ifPresent(m -> {
                    throw new ReviewException(ReviewErrorCode.REVIEW_ALREADY_EXISTS_ERROR.getMessage());
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

    public Review updateReview(Long id, String content) {
        // 리뷰 존재 확인
        Review review = existsById(id);
        // 작성자 확인
        checkAuthorMatch(review);

        review.setContent(content);
        repository.update(review.getId(), content);

        return review;
    }

    private void checkAuthorMatch(Review review) {
        String currentUsername = (String) session.getAttribute("currentUser");
        if (currentUsername == null || !review.getAuthor().equals(currentUsername)) {
            throw new ReviewException(ReviewErrorCode.INACTIVE_USER_ERROR.getMessage());
        }
    }

    public void deleteReview(Long id) {
        // 리뷰 존재 확인
        Review review = existsById(id);
        // 작성자 확인
        checkAuthorMatch(review);

        repository.deleteById(id);
    }

    private Review existsById(Long id) {
        Review review = repository.findById(id)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND_ERROR.getMessage()));
        return review;
    }
}
