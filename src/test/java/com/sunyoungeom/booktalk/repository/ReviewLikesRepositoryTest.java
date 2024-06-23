package com.sunyoungeom.booktalk.repository;

import com.sunyoungeom.booktalk.domain.ReviewLikes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ReviewLikesRepositoryTest {

    @Autowired
    ReviewLikesRepository repository;

    @Test
    @Transactional
    void 저장() {
        ReviewLikes reviewLikes = new ReviewLikes(1L, 1L);

        ReviewLikes savedReviewLikes = repository.save(reviewLikes);

        List<ReviewLikes> result = repository.findAll();
        assertThat(result.size()).isEqualTo(1);
    }
    @Test
    @Transactional
    void 취소() {
        ReviewLikes reviewLikes = new ReviewLikes(1L, 1L);
        ReviewLikes savedReviewLikes = repository.save(reviewLikes);

        repository.delete(savedReviewLikes.getUserId(), savedReviewLikes.getReviewId());

        List<ReviewLikes> result = repository.findAll();
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    @Transactional
    void 유저아이디로_찾기() {
        ReviewLikes reviewLikes = new ReviewLikes(1L, 1L);
        ReviewLikes savedReviewLikes = repository.save(reviewLikes);

        List<ReviewLikes> result = repository.findReviewLikesByUserId(savedReviewLikes.getUserId());

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(savedReviewLikes.getId());
    }
}