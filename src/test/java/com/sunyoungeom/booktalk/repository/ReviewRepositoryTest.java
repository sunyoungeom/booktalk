package com.sunyoungeom.booktalk.repository;

import com.sunyoungeom.booktalk.domain.Review;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ReviewRepositoryTest {

    ReviewRepositoryMemory repository = new ReviewRepositoryMemory();

    @AfterEach
    public void clear() {
        repository.clearStore();
    }

    @Test
    void 저장() {
        Review review = new Review(0L, "리뷰제목", "리뷰작성자", "리뷰내용");

        Review save = repository.save(review);

        Review result = repository.findById(review.getId()).get();
        assertThat(result.getId()).isEqualTo(save.getId());
    }

    @Test
    void 중복_저장_예외() {
        Review review = new Review(0L, "리뷰제목", "리뷰작성자", "리뷰내용");
        repository.save(review);

        List<Review> byUserId = repository.findByUserId(review.getUserId());
        Review result = byUserId.get(0);
        boolean b = repository.existsByTitleAndUserId(result.getTitle(), result.getUserId());

        assertThat(byUserId.size()).isEqualTo(1);
        assertThat(b).isTrue();
    }

    @Test
    void 수정() {
        Review review = new Review(0L, "리뷰제목", "리뷰작성자", "리뷰내용");
        repository.save(review);

        String 수정전내용 = review.getContent();
        String 수정후내용 = "리뷰수정내용";
        repository.update(review.getId(), 수정후내용);

        Review result = repository.findById(review.getId()).get();
        assertThat(result.getId()).isEqualTo(review.getId());
        assertThat(수정전내용).isNotEqualTo(result.getContent());
    }

    @Test
    void 전체리뷰_인기순_정렬() {
        Review review = new Review(0L, "리뷰제목1", "리뷰작성자1", "리뷰내용1");
        Review review2 = new Review(0L, "리뷰제목2", "리뷰작성자2", "리뷰내용2");
        review.setLikes(1);
        review2.setLikes(0);
        repository.save(review);
        repository.save(review2);

        List<Review> result = repository.findAllOrderByLikesDesc();

        assertThat(result.get(0).getLikes()).isGreaterThan(result.get(1).getLikes());
    }

    @Test
    void 전체리뷰_최신순_정렬() {
        Review review = new Review(0L, "리뷰제목1", "리뷰작성자1", "리뷰내용1");
        Review review2 = new Review(0L, "리뷰제목2", "리뷰작성자2", "리뷰내용2");
        repository.save(review);
        repository.save(review2);

        List<Review> result = repository.findAllOrderByDateDesc();

        assertThat(result.get(0).getDate()).isGreaterThan(result.get(1).getDate());
    }

    @Test
    void 제목_검색_리뷰_인기순_정렬() {
        Review review = new Review(0L, "리뷰제목1", "리뷰작성자1", "리뷰내용1");
        Review review2 = new Review(0L, "리뷰제목2", "리뷰작성자2", "리뷰내용2");
        Review review3 = new Review(0L, "리뷰제목2", "리뷰작성자2", "리뷰내용2");
        review.setLikes(1);
        review2.setLikes(0);
        review3.setLikes(3);
        repository.save(review);
        repository.save(review2);
        repository.save(review3);

        List<Review> result = repository.findByTitleOrderByLikesDesc("리뷰제목2");

        assertThat(result.get(0).getLikes()).isGreaterThan(result.get(1).getLikes());
    }

    @Test
    void 제목_검색_리뷰_최신순_정렬() {
        Review review = new Review(0L, "리뷰제목1", "리뷰작성자1", "리뷰내용1");
        Review review2 = new Review(0L, "리뷰제목2", "리뷰작성자2", "리뷰내용2");
        Review review3 = new Review(0L, "리뷰제목2", "리뷰작성자1", "리뷰내용2");
        repository.save(review);
        repository.save(review2);
        repository.save(review3);

        List<Review> result = repository.findByTitleOrderByDateDesc("리뷰제목2");

        assertThat(result.get(0).getDate()).isGreaterThan(result.get(1).getDate());
    }

    @Test
    void 작성자별_리뷰조회() {
        Review review = new Review(0L, "리뷰제목1", "리뷰작성자1", "리뷰내용1");
        Review review2 = new Review(0L, "리뷰제목2", "리뷰작성자1", "리뷰내용2");
        Review review3 = new Review(1L, "리뷰제목2", "리뷰작성자2", "리뷰내용2");
        repository.save(review);
        repository.save(review2);
        repository.save(review3);

        List<Review> result = repository.findByUserId(review.getUserId());

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void 존재하는_리뷰_삭제() {
        Review review = new Review(0L, "리뷰제목", "리뷰작성자", "리뷰내용");
        repository.save(review);

        Long reviewId = review.getId();
        repository.deleteById(reviewId);

        assertThat(repository.findById(reviewId)).isEmpty();
    }

    @Test
    void 존재하지않는_리뷰_삭제() {
        Long reviewId = 10L;

        assertDoesNotThrow(() -> repository.deleteById(reviewId));

        assertThat(repository.findById(reviewId).isEmpty());
    }
}