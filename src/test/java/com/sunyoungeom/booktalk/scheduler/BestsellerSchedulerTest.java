package com.sunyoungeom.booktalk.scheduler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunyoungeom.booktalk.dto.ReviewLikesDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;

class BestsellerSchedulerTest {
    private final String FILE_PATH = "text.json";
    private final Object lockObject = new Object();
    int count = 0;
    int lockCount = 0;

    @BeforeEach
    void setUp() throws IOException {
        // 초기 데이터 설정
        ReviewLikesDTO reviewLikesDTO = new ReviewLikesDTO();
        reviewLikesDTO.setLikes(1);
        reviewLikesDTO.setLiked(true);
        save(reviewLikesDTO);
    }

    @Test
    @DisplayName("락 없이 테스트")
    public void test1() throws InterruptedException, IOException {
        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 1초 후에 메서드 실행
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(this::update, 1, SECONDS);

        Thread.sleep(900);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    load();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드의 작업이 완료될 때까지 대기
        latch.await();
        System.out.println(count);
    }

    @Test
    @DisplayName("락 적용후 테스트")
    public void test2() throws InterruptedException, IOException {
        int count = 0;
        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 1초 후에 메서드 실행
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(this::updateWithLock, 1, SECONDS);

        Thread.sleep(900);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    loadWithLock();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드의 작업이 완료될 때까지 대기
        latch.await();
        System.out.println(lockCount);
    }

    void update() {
        // 테스트 데이터 업데이트
        try {
            delete(new File(FILE_PATH));
            ReviewLikesDTO reviewLikesDTO = new ReviewLikesDTO();
            reviewLikesDTO.setLikes(2);
            reviewLikesDTO.setLiked(false);
            save(reviewLikesDTO);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    void updateWithLock() {
        // 테스트 데이터 업데이트
        synchronized (lockObject) {
            try (RandomAccessFile raf = new RandomAccessFile(new File(FILE_PATH), "rw");
                 FileChannel channel = raf.getChannel();
                 FileLock lock = channel.lock()) {

                delete(new File(FILE_PATH));
                ReviewLikesDTO reviewLikesDTO = new ReviewLikesDTO();
                reviewLikesDTO.setLikes(2);
                reviewLikesDTO.setLiked(false);
                save(reviewLikesDTO);

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

        }
    }

    void delete(File file) throws IOException {
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("파일 삭제 완료");
            } else {
                System.out.println("파일 삭제 실패");
            }
        }
    }

    ReviewLikesDTO load() {
        ReviewLikesDTO reviewLikesDTO = new ReviewLikesDTO();
        File file = new File(FILE_PATH);
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            if (file.exists()) {
                ObjectMapper objectMapper = new ObjectMapper();
                reviewLikesDTO = objectMapper.readValue(file, new TypeReference<ReviewLikesDTO>() {
                });
                System.out.println(reviewLikesDTO.toString());
                count++;
            } else {
                throw new RuntimeException("파일이 존재하지 않습니다.");
            }
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
        return reviewLikesDTO;
    }

    void save(ReviewLikesDTO reviewLikesDTO) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(FILE_PATH);
        objectMapper.writeValue(file, reviewLikesDTO);
    }

    ReviewLikesDTO loadWithLock() {
        ReviewLikesDTO reviewLikesDTO = new ReviewLikesDTO();
        File file = new File(FILE_PATH);
        synchronized (lockObject) {
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
                 FileChannel channel = raf.getChannel();
                 FileLock lock = channel.lock()) {

                if (file.exists() && lock != null) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    if (file.length() > 0) {
                        reviewLikesDTO = objectMapper.readValue(file, new TypeReference<ReviewLikesDTO>() {
                        });
                        System.out.println(reviewLikesDTO.toString());
                        lockCount++;
                    } else {
                        throw new RuntimeException("파일이 비어 있습니다.");
                    }
                } else {
                    throw new IOException("락 획득 실패");
                }
            } catch (IOException | RuntimeException e) {
                e.printStackTrace();
            }
        }
        return reviewLikesDTO;
    }
}