package com.sunyoungeom.booktalk.repository;

import com.sunyoungeom.booktalk.domain.Review;
import com.sunyoungeom.booktalk.dto.ReviewDTO;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
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

    public List<ReviewDTO> findAllOrderByDateDesc(Long userId, RowBounds rowBounds) {
        return sql.selectList("Review.findAllOrderByDateDesc", userId, rowBounds);
    }

    public List<ReviewDTO> findAllOrderByLikesDesc(Long userId, RowBounds rowBounds) {
        return sql.selectList("Review.findAllOrderByLikesDesc", userId, rowBounds);
    }

    public List<ReviewDTO> findByTitleOrderByDateDesc(Long userId, String title, RowBounds rowBounds) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("title", title);
        return sql.selectList("Review.findByTitleOrderByDateDesc", params, rowBounds);
    }

    public List<ReviewDTO> findByTitleOrderByLikesDesc(Long userId, String title, RowBounds rowBounds) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("title", title);
        return sql.selectList("Review.findByTitleOrderByLikesDesc", params, rowBounds);
    }

    public int countReviews(Long userId) {
        return sql.selectOne("Review.countReviews", userId);
    }

    public int countReviewsByTitle(Long userId, String title) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("title", title);
        return sql.selectOne("Review.countReviewsByTitle", params);
    }

    public List<Review> findByUserId(Long userId) {
        return sql.selectList("Review.findByUserId", userId);
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
