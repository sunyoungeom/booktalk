package com.sunyoungeom.booktalk.repository;

import com.sunyoungeom.booktalk.domain.ReviewLikes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewLikesRepositoryTest {

    @Autowired
    ReviewLikesRepositoryMemory repository = new ReviewLikesRepositoryMemory();

    @Test
    void 저장() {
        ReviewLikes reviewLikes = new ReviewLikes(1L, 1L); // 새로운 ReviewLikes 객체 생성

        ReviewLikes savedReviewLikes = repository.save(reviewLikes); // 저장

        List<ReviewLikes> result = repository.findAll();
        assertThat(result.size()).isEqualTo(1);
    }
    @Test
    void 취소() {
        ReviewLikes reviewLikes = new ReviewLikes(1L, 1L); // 새로운 ReviewLikes 객체 생성
        ReviewLikes savedReviewLikes = repository.save(reviewLikes); // 저장

        repository.delete(savedReviewLikes.getUserId(), savedReviewLikes.getReviewId());

        List<ReviewLikes> result = repository.findAll();
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void 유저아이디로_찾기() {
        ReviewLikes reviewLikes = new ReviewLikes(1L, 1L); // 새로운 ReviewLikes 객체 생성
        ReviewLikes savedReviewLikes = repository.save(reviewLikes); // 저장

        List<ReviewLikes> result = repository.findReviewLikesByUserId(savedReviewLikes.getUserId());

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(savedReviewLikes.getId());
    }
}