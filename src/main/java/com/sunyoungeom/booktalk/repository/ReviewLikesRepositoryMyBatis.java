package com.sunyoungeom.booktalk.repository;

import com.sunyoungeom.booktalk.domain.ReviewLikes;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewLikesRepositoryMyBatis implements ReviewLikesRepository {

    private final SqlSessionTemplate sql;

    @Override
    public ReviewLikes save(ReviewLikes reviewLikes) {
        sql.insert("ReviewLikes.save", reviewLikes);
        return reviewLikes;
    }

    @Override
    public List<ReviewLikes> findAll() {
        return sql.selectList("ReviewLikes.findAll");
    }

    @Override
    public Optional<ReviewLikes> findById(Long id) {
        ReviewLikes reviewLikes = sql.selectOne("ReviewLikes.findById", id);
        return Optional.ofNullable(reviewLikes);
    }

    @Override
    public List<ReviewLikes> findReviewLikesByUserId(Long userId) {
        return sql.selectList("ReviewLikes.findReviewLikesByUserId", userId);
    }

    @Override
    public boolean findByUserIdAndReviewId(Long userId, Long reviewId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("reviewId", reviewId);
        Integer count = sql.selectOne("ReviewLikes.findByUserIdAndReviewId", params);
        return count != null && count > 0;
    }

    @Override
    public boolean findByUserId(Long userId) {
        Integer count = sql.selectOne("ReviewLikes.findByUserId", userId);
        return count != null && count > 0;
    }

    @Override
    public void delete(Long userId, Long reviewId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("reviewId", reviewId);
        Integer count = sql.selectOne("ReviewLikes.delete", params);
    }
}
