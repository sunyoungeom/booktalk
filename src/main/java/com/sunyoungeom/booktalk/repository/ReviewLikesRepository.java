package com.sunyoungeom.booktalk.repository;

import com.sunyoungeom.booktalk.domain.ReviewLikes;
import com.sunyoungeom.booktalk.dto.ReviewDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ReviewLikesRepository {

    private final SqlSessionTemplate sql;

    public int saveOrUpdateLike(Long userId, Long reviewId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("reviewId", reviewId);
        return sql.insert("ReviewLikes.saveOrUpdateLike", params);
    }

    public List<ReviewDTO> findLikedReviewsByUserId(Long userId, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("pageable", pageable);
        return sql.selectList("ReviewLikes.findLikedReviewsByUserId", params);
    }

    public int countLikedReviews(Long userId) {
        return sql.selectOne("ReviewLikes.countLikedReviews", userId);
    }

    public boolean findByUserIdAndReviewId(Long userId, Long reviewId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("reviewId", reviewId);
        Integer result = sql.selectOne("ReviewLikes.findByUserIdAndReviewId", params);
        return result != null && result == 1;
    }

    public void delete(Long userId, Long reviewId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("reviewId", reviewId);
        Integer count = sql.selectOne("ReviewLikes.delete", params);
    }

    public void deleteReview(Long reviewId) {
        sql.delete("ReviewLikes.deleteReview", reviewId);
    }
}
