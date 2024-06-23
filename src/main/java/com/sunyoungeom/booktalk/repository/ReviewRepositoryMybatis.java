package com.sunyoungeom.booktalk.repository;

import com.sunyoungeom.booktalk.domain.Review;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//@Repository
@RequiredArgsConstructor
public class ReviewRepositoryMybatis implements ReviewRepository {

    private final SqlSessionTemplate sql;

    @Override
    public Review save(Review review) {
        sql.insert("Review.save", review);
        return review;
    }

    @Override
    public Optional<Review> findById(Long id) {
        Review review = sql.selectOne("Review.findById", id);
        return Optional.ofNullable(review);
    }

    @Override
    public boolean existsByTitleAndUserId(String title, Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("title", title);
        params.put("userId", userId);
        Integer count = sql.selectOne("Review.existsByTitleAndUserId", params);
        return count != null && count > 0;
    }

    @Override
    public List<Review> findAllOrderByDateDesc() {
        return sql.selectList("Review.findAllOrderByDateDesc");
    }

    @Override
    public List<Review> findAllOrderByLikesDesc() {
        return sql.selectList("Review.findAllOrderByLikesDesc");
    }

    @Override
    public List<Review> findByTitleOrderByDateDesc(String title) {
        return sql.selectList("Review.findByTitleOrderByDateDesc", title);
    }

    @Override
    public List<Review> findByTitleOrderByLikesDesc(String title) {
        return sql.selectList("Review.findByTitleOrderByLikesDesc", title);
    }

    @Override
    public List<Review> findByUserId(Long userId) {
        return sql.selectList("Review.findByUserId", userId);
    }

    @Override
    public void update(Long id, String content) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("content", content);
        sql.update("Review.update", params);
    }

    @Override
    public void delete(Long id) {
        sql.delete("Review.delete", id);
    }

    @Override
    public void clearStroe() {

    }
}
