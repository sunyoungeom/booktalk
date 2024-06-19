package com.sunyoungeom.booktalk.service;

import com.sunyoungeom.booktalk.domain.Review;
import com.sunyoungeom.booktalk.exception.ReviewException;
import com.sunyoungeom.booktalk.repository.MemoryReviewRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class ReviewServiceTest {

    ReviewService service;
    MemoryReviewRepository repository;
    HttpSession session;

    @BeforeEach
    public void beforeEach() {
        repository = new MemoryReviewRepository();
        service = new ReviewService(repository);
        session = new MockHttpSession();
        session.setAttribute("currentUser", "test1");
    }

    @AfterEach
    public void clear() {
        repository.clearStore();
    }

    @Test
    public void 리뷰_생성() {
        Review review = new Review();
        review.setTitle("리뷰제목");

        Review createdReview = service.createReview(review, (String) session.getAttribute("currentUser"));

        Review result = repository.findById(createdReview.getId()).get();
        assertEquals(review.getTitle(), result.getTitle());
    }

    @Test
    public void 중복_리뷰_생성_예외() {
        Review review1 = new Review();
        review1.setTitle("리뷰제목");

        Review review2 = new Review();
        review2.setTitle("리뷰제목");

       String author = (String) session.getAttribute("currentUser");

        service.createReview(review1, author);
        ReviewException e = assertThrows(ReviewException.class, () -> service.createReview(review2, author));

        assertThat(e.getMessage()).isEqualTo("이미 작성한 리뷰입니다.");
    }

    @Test
    void 전체조회() {
        Review review = new Review("리뷰제목1", "리뷰작성자1", String.valueOf(LocalDate.now()), "리뷰내용1", 1);
        Review review2 = new Review("리뷰제목2", "리뷰작성자1", String.valueOf(LocalDate.now().minusDays(1)), "리뷰내용2", 0);
        Review review3 = new Review("리뷰제목2", "리뷰작성자2", String.valueOf(LocalDate.now().minusDays(1)), "리뷰내용2", 1);
        service.createReview(review, review.getAuthor());
        service.createReview(review2, review2.getAuthor());
        service.createReview(review3, review3.getAuthor());

        List<Review> reviews = service.findAll();

        assertThat(reviews.size()).isEqualTo(3);
    }

    @Test
    void 인기순_조회() {
        Review review = new Review("리뷰제목1", "리뷰작성자1", String.valueOf(LocalDate.now()), "리뷰내용1", 1);
        Review review2 = new Review("리뷰제목2", "리뷰작성자1", String.valueOf(LocalDate.now().minusDays(1)), "리뷰내용2", 0);
        Review review3 = new Review("리뷰제목2", "리뷰작성자2", String.valueOf(LocalDate.now().minusDays(1)), "리뷰내용2", 1);
        service.createReview(review, review.getAuthor());
        service.createReview(review2, review2.getAuthor());
        service.createReview(review3, review3.getAuthor());

        List<Review> reviews = service.findAllSortedByLikes();
        System.out.println(reviews.toString());

        assertThat(reviews.size()).isEqualTo(3);
    }

    @Test
    void 존재하는_리뷰_수정() {
        Review review = new Review("리뷰제목1", "test1", String.valueOf(LocalDate.now()), "리뷰내용1", 1);
        service.createReview(review, (String) session.getAttribute("currentUser"));

        service.updateReview(review.getId(), "수정내용", (String) session.getAttribute("currentUser"));

        assertThat(review.getContent()).isEqualTo("수정내용");
    }

    @Test
    void 존재하지않는_리뷰_수정() {
        Long reviewId = 10L;

        ReviewException e = assertThrows(ReviewException.class, () -> service.updateReview(reviewId, "수정내용", (String) session.getAttribute("currentUser")));

        assertThat(e.getMessage()).isEqualTo("존재하지 않는 리뷰입니다.");
    }

    @Test
    void 존재하는_리뷰_삭제() {
        Review review = new Review("리뷰제목1", "리뷰작성자1", String.valueOf(LocalDate.now()), "리뷰내용1", 1);
        service.createReview(review, (String) session.getAttribute("currentUser"));

        service.deleteReview(review.getId(), (String) session.getAttribute("currentUser"));

        assertThat(service.findByAuthor(review.getAuthor())).isEmpty();
    }

    @Test
    void 존재하지않는_리뷰_삭제() {
        Long reviewId = 10L;

        ReviewException e = assertThrows(ReviewException.class, () -> service.deleteReview(reviewId, (String) session.getAttribute("currentUser")));

        assertThat(e.getMessage()).isEqualTo("존재하지 않는 리뷰입니다.");
    }

    @Test
    void 권한없는_리뷰_삭제() {
        Review review = new Review("리뷰제목1", "리뷰작성자1", String.valueOf(LocalDate.now()), "리뷰내용1", 1);
        service.createReview(review, (String) session.getAttribute("currentUser"));

        ReviewException e = assertThrows(ReviewException.class, () -> service.deleteReview(review.getId(), "작성자가_아닌_사용자"));
        assertThat(e.getMessage()).isEqualTo("권한이 없습니다.");
    }



}