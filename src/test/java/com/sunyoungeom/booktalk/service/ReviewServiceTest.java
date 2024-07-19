package com.sunyoungeom.booktalk.service;

import com.sunyoungeom.booktalk.BooktalkApplication;
import com.sunyoungeom.booktalk.domain.Review;
import com.sunyoungeom.booktalk.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = BooktalkApplication.class)
class ReviewServiceTest {

    @Autowired
    ReviewService reviewService;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReviewLikesRepository reviewLikesRepository;

    @Test
    @Rollback
    @Transactional
    void 동시성_테스트() throws InterruptedException {
        int numberOfThreads = 100;
        CountDownLatch finishLatch = new CountDownLatch(numberOfThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            Long userId = (long) i + 200;
            executorService.execute(() -> {
                try {
                    reviewService.likeReview(1L, userId);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        finishLatch.await(20, TimeUnit.SECONDS);

        Review review = reviewRepository.findById(1L).get();
        System.out.println("Thread 갯수: " + numberOfThreads);
        System.out.println("실제 좋아요 수: " + review.getLikes());
    }
}
