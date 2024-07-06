package com.sunyoungeom.booktalk.repository;

import com.sunyoungeom.booktalk.domain.Review;
import com.sunyoungeom.booktalk.dto.ReviewDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewRepository {

    private final SqlSessionTemplate sql;

    public Review save(Review review) {
        sql.insert("Review.save", review);
        return review;
    }

    public Optional<Review> findById(Long id) {
        Review review = sql.selectOne("Review.findById", id);
        return Optional.ofNullable(review);
    }

    public Optional<Review> existsByTitleAndUserId(String title, Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("title", title);
        params.put("userId", userId);
        Review review = sql.selectOne("Review.existsByTitleAndUserId", params);
        return Optional.ofNullable(review);
    }

    public List<ReviewDTO> findAllOrderByDateDesc(Long userId, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("pageable", pageable);
        return sql.selectList("Review.findAllOrderByDateDesc", params);
    }

    public List<ReviewDTO> findAllOrderByLikesDesc(Long userId, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("pageable", pageable);
        return sql.selectList("Review.findAllOrderByLikesDesc", params);
    }

    public List<ReviewDTO> findByTitleOrderByDateDesc(Long userId, String title, String author, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("title", title);
        params.put("author", author);
        params.put("pageable", pageable);
        return sql.selectList("Review.findByTitleOrderByDateDesc", params);
    }

    public List<ReviewDTO> findByTitleOrderByLikesDesc(Long userId, String title, String author, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("title", title);
        params.put("author", author);
        params.put("pageable", pageable);
        return sql.selectList("Review.findByTitleOrderByLikesDesc", params);
    }

    public int countReviews() {
        return sql.selectOne("Review.countReviews");
    }

    public int countReviewsByTitle(String title) {
        return sql.selectOne("Review.countReviewsByTitle", title);
    }

    public int countReviewsByUserId(Long userId) {
        return sql.selectOne("Review.countReviewsByUserId", userId);
    }

    public List<ReviewDTO> findByUserId(Long userId, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("pageable", pageable);
        return sql.selectList("Review.findByUserId", params);
    }

    public void increaseLikes(Long id) {
        sql.update("Review.increaseLikes", id);
    }

    public void decreaseLikes(Long id) {
        sql.update("Review.decreaseLikes", id);
    }

    public void update(Long id, String content) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("content", content);
        sql.update("Review.update", params);
    }

    public void delete(Long id) {
        sql.delete("Review.delete", id);
    }
}
