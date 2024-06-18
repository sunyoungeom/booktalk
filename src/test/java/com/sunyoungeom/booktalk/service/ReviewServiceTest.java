package com.sunyoungeom.booktalk.service;

import com.sunyoungeom.booktalk.domain.Review;
import com.sunyoungeom.booktalk.repository.MemoryReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class ReviewServiceTest {

    ReviewService service;
    MemoryReviewRepository repository;

    @BeforeEach
    public void beforeEach() {
        repository = new MemoryReviewRepository();
        service = new ReviewService(repository);
    }

    @AfterEach
    public void clear() {
        repository.clearStore();
    }

    @Test
    public void 리뷰_생성() {
        Review review = new Review();
        review.setTitle("리뷰제목");

        Review createdReview = service.createReview(review);

        Review result = repository.findById(createdReview.getId()).get();
        assertEquals(review.getTitle(), result.getTitle());
    }

    @Test
    public void 중복_리뷰_생성_예외() {
        Review review1 = new Review();
        review1.setTitle("리뷰제목");
        review1.setAuthor("작성자");

        Review review2 = new Review();
        review2.setTitle("리뷰제목");
        review2.setAuthor("작성자");

        service.createReview(review1);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> service.createReview(review2));

        assertThat(e.getMessage()).isEqualTo("이미 작성한 리뷰입니다.");
    }

    @Test
    void 전체조회() {
        Review review = new Review("리뷰제목1", "리뷰작성자1", String.valueOf(LocalDate.now()), "리뷰내용1", 1);
        Review review2 = new Review("리뷰제목2", "리뷰작성자1", String.valueOf(LocalDate.now().minusDays(1)), "리뷰내용2", 0);
        Review review3 = new Review("리뷰제목2", "리뷰작성자2", String.valueOf(LocalDate.now().minusDays(1)), "리뷰내용2", 1);
        service.createReview(review);
        service.createReview(review2);
        service.createReview(review3);

        List<Review> reviews = service.findAll();

        assertThat(reviews.size()).isEqualTo(3);
    }

    @Test
    void 인기순_조회() {
        Review review = new Review("리뷰제목1", "리뷰작성자1", String.valueOf(LocalDate.now()), "리뷰내용1", 1);
        Review review2 = new Review("리뷰제목2", "리뷰작성자1", String.valueOf(LocalDate.now().minusDays(1)), "리뷰내용2", 0);
        Review review3 = new Review("리뷰제목2", "리뷰작성자2", String.valueOf(LocalDate.now().minusDays(1)), "리뷰내용2", 1);
        service.createReview(review);
        service.createReview(review2);
        service.createReview(review3);

        List<Review> reviews = service.findAllSortedByLikes();
        System.out.println(reviews.toString());

        assertThat(reviews.size()).isEqualTo(3);
    }

    @Test
    void 존재하는_리뷰_수정() {
        Review review = new Review("리뷰제목1", "리뷰작성자1", String.valueOf(LocalDate.now()), "리뷰내용1", 1);
        service.createReview(review);

        service.updateReview(review.getId(), "수정내용");

        assertThat(review.getContent()).isEqualTo("수정내용");
    }

    @Test
    void 존재하지않는_리뷰_수정() {
        Long reviewId = 10L;

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> service.updateReview(reviewId, "수정내용"));

        assertThat(e.getMessage()).isEqualTo("해당 ID의 리뷰를 찾을 수 없습니다.");
    }

    @Test
    void 존재하는_리뷰_삭제() {
        Review review = new Review("리뷰제목1", "리뷰작성자1", String.valueOf(LocalDate.now()), "리뷰내용1", 1);
        service.createReview(review);

        service.deleteReview(review.getId());

        assertThat(service.findByAuthor(review.getAuthor())).isEmpty();
    }

    @Test
    void 존재하지않는_리뷰_삭제() {
        Long reviewId = 10L;

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> service.deleteReview(reviewId));

        assertThat(e.getMessage()).isEqualTo("해당 ID의 리뷰를 찾을 수 없습니다.");
    }

}