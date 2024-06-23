package com.sunyoungeom.booktalk.service;

import com.sunyoungeom.booktalk.domain.Review;
import com.sunyoungeom.booktalk.domain.ReviewLikes;
import com.sunyoungeom.booktalk.exception.ReviewException;
import com.sunyoungeom.booktalk.facade.ReviewFacade;
import com.sunyoungeom.booktalk.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class ReviewServiceTest {

    ReviewService service;
    ReviewFacade reviewFacade;
    ReviewRepositoryMemory reviewRepository = new ReviewRepositoryMemory();
    ReviewLikesRepositoryMemory reviewLikesRepository = new ReviewLikesRepositoryMemory();

    @BeforeEach
    public void beforeEach() {
        reviewFacade = new ReviewFacade(reviewRepository, reviewLikesRepository);
        service = new ReviewService(reviewFacade);
    }

    @AfterEach
    public void clear() {
        reviewFacade.clearStore();
    }

    @Test
    public void 리뷰_생성() {
        Review review = new Review(0L, "리뷰제목", "닉네임", "리뷰내용");
        Review createdReview = service.createReview(review, review.getUserId(), review.getAuthor());

        Review result = reviewFacade.findReviewById(createdReview.getId()).get();
        assertEquals(review.getTitle(), result.getTitle());
    }

    @Test
    public void 중복_리뷰_생성_예외() {
        Review review = new Review(0L, "리뷰제목", "닉네임", "리뷰내용");
        Review createdReview = service.createReview(review, review.getUserId(), review.getAuthor());

        Review review2 = new Review(createdReview.getUserId(), createdReview.getTitle(), createdReview.getAuthor(), createdReview.getContent());
        ReviewException e = assertThrows(ReviewException.class, () ->
                service.createReview(review2, createdReview.getUserId(), createdReview.getAuthor()));

        assertThat(e.getMessage()).isEqualTo("이미 작성한 리뷰입니다.");
    }

    @Test
    void 전체조회() {
        Review review1 = new Review(0L, "리뷰제목", "닉네임", "리뷰내용");
        Review review2 = new Review(1L, "리뷰제목", "닉네임", "리뷰내용");
        service.createReview(review1, review1.getUserId(), review1.getAuthor());
        service.createReview(review2, review2.getUserId(), review2.getAuthor());

        List<Review> reviews = service.findAllOrderByDateDesc();

        assertThat(reviews.size()).isEqualTo(2);
    }

    @Test
    void 인기순_조회() {
        Review review1 = new Review(0L, "리뷰제목", "닉네임", "리뷰내용");
        Review review2 = new Review(1L, "리뷰제목", "닉네임", "리뷰내용");
        Review createdReview1 = service.createReview(review1, review1.getUserId(), review1.getAuthor());
        service.createReview(review2, review2.getUserId(), review2.getAuthor());
        service.likeReview(createdReview1.getId(), 1L);

        List<Review> result = service.findAllOrderByLikesDesc();
        System.out.println(result.toString());
        assertThat(result.get(0).getLikes()).isGreaterThan(result.get(1).getLikes());
    }

    @Test
    void 존재하는_리뷰_수정() {
        Long userId = 0L;
        Review review = new Review(userId, "리뷰제목", "닉네임", "리뷰내용");
        Review createdReview = service.createReview(review, review.getUserId(), review.getAuthor());

        service.update(createdReview.getId(), createdReview.getUserId(), "수정내용");

        assertThat(createdReview.getContent()).isEqualTo("수정내용");
    }

    @Test
    void 존재하지않는_리뷰_수정() {
        Long reviewId = 10L;
        Long userId = 10L;

        ReviewException e = assertThrows(ReviewException.class, () -> service.update(reviewId, userId, "수정내용"));

        assertThat(e.getMessage()).isEqualTo("존재하지 않는 리뷰입니다.");
    }

    @Test
    void 존재하는_리뷰_삭제() {
        Review review = new Review(0L, "리뷰제목", "닉네임", "리뷰내용");
        Review createdReview = service.createReview(review, review.getUserId(), review.getAuthor());

        service.deleteReview(createdReview.getId(), createdReview.getUserId());
        assertThat(service.findAllOrderByDateDesc().isEmpty());
    }

    @Test
    void 존재하지않는_리뷰_삭제() {
        Long reviewId = 10L;
        Long userId = 10L;

        ReviewException e = assertThrows(ReviewException.class, () -> service.deleteReview(reviewId, userId));

        assertThat(e.getMessage()).isEqualTo("존재하지 않는 리뷰입니다.");
    }

    @Test
    void 권한없는_리뷰_삭제() {
        Review review = new Review(0L, "리뷰제목", "닉네임", "리뷰내용");
        Review createdReview = service.createReview(review, review.getUserId(), review.getAuthor());

        ReviewException e = assertThrows(ReviewException.class, () -> service.deleteReview(review.getId(), 1L));
        assertThat(e.getMessage()).isEqualTo("권한이 없습니다.");
    }


    @Test
    void 좋아요() {
        Long userid = 1L;
        Review review = new Review(0L, "리뷰제목", "닉네임", "리뷰내용");
        Review createdReview = service.createReview(review, review.getUserId(), review.getAuthor());

        service.likeReview(createdReview.getId(), userid);

        List<ReviewLikes> resultLikes = service.findAllReviewLikes();
        List<Review> resultReviews = service.findAllOrderByDateDesc();
        assertThat(resultLikes.size()).isEqualTo(1);
        assertThat(resultReviews.size()).isEqualTo(1);
        assertThat(resultReviews.get(0).getLikes()).isEqualTo(1);
    }

    @Test
    void 없는_리뷰에_좋아요() {
        Long userid = 1L;

        ReviewException e = assertThrows(ReviewException.class, () -> service.likeReview(0L, userid));
        assertThat(e.getMessage()).isEqualTo("존재하지 않는 리뷰입니다.");
    }

    @Test
    void 본인작성_리뷰에_좋아요() {
        Long userid = 0L;
        Review review = new Review(0L, "리뷰제목", "닉네임", "리뷰내용");
        Review createdReview = service.createReview(review, review.getUserId(), review.getAuthor());

        ReviewException e = assertThrows(ReviewException.class, () -> service.likeReview(createdReview.getId(), userid));
        assertThat(e.getMessage()).isEqualTo("본인이 쓴 리뷰에는 좋아요를 누를 수 없습니다.");
    }

    @Test
    void 이미_리뷰에_좋아요() {
        Long userid = 1L;
        Review review = new Review(0L, "리뷰제목", "닉네임", "리뷰내용");
        Review createdReview = service.createReview(review, review.getUserId(), review.getAuthor());

        service.likeReview(createdReview.getId(), userid);
        service.likeReview(createdReview.getId(), userid);

        List<ReviewLikes> result = service.findAllReviewLikes();
        assertThat(result.size()).isEqualTo(0);
    }

}