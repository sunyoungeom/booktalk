package com.sunyoungeom.booktalk.scheduler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@EnableScheduling
@Slf4j
public class BestsellerScheduler {
    private static final String FILE_DIRECTORY = "file/best";
    private static final String FILE_NAME = "bestseller_%s.json";
    private final Object lockObject = new Object();


    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 업데이트
    public void updateBestsellers() {
        synchronized (lockObject) {
            String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String fileName = String.format(FILE_NAME, formattedDate);
            Path filePath = Paths.get(FILE_DIRECTORY, fileName);

            try (RandomAccessFile raf = new RandomAccessFile(new File(FILE_DIRECTORY, fileName), "rw");
                 FileChannel channel = raf.getChannel();
                 FileLock lock = channel.lock()) {
                log.info("베스트셀러 업데이트 시작");

                // 기존 파일 삭제
                deleteExistingFile();

                // 베스트셀러 업데이트
                List<Map<String, String>> bestsellers = fetchBestsellers();
                saveBestsellersToFile(bestsellers, filePath);

                log.info("베스트셀러 업데이트 완료");
            } catch (IOException e) {
                log.error("베스트셀러 업데이트 시 에러 발생", e);
            }
        }
    }

    private void deleteExistingFile() {
        try {
            File directory = new File(FILE_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File[] files = directory.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        log.info("기존 파일 삭제 완료: {}", file.getName());
                    } else {
                        log.error("기존 파일 삭제 실패: {}", file.getName());
                    }
                }
            } else {
                throw new RuntimeException();
            }
        } catch (RuntimeException e) {
            log.info("삭제할 파일이 없습니다.", e);
        }
    }

    private List<Map<String, String>> fetchBestsellers() throws IOException {
        List<Map<String, String>> bestsellers = new ArrayList<>();
        String url = "https://www.yes24.com/Product/Category/BestSeller?categoryNumber=001&pageNumber=1&pageSize=24";
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select(".img_bdr .lazy");
        Elements elements1 = doc.select("span.authPub.info_auth");
        Elements elements2 = doc.select("span.authPub.info_date");

        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            Element element1 = elements1.get(i);
            Element element2 = elements2.get(i);

            String title = element.attr("alt");
            String imgPath = element.attr("data-original");
            String author = element1.text();
            String date = element2.text().replace("년 ", "-").replace("월", "");

            Map<String, String> elementMap = new HashMap<>();
            elementMap.put("title", title);
            elementMap.put("imgPath", imgPath);
            elementMap.put("author", author);
            elementMap.put("date", date);

            bestsellers.add(elementMap);
        }
        return bestsellers;
    }

    private void saveBestsellersToFile(List<Map<String, String>> bestsellers, Path filePath) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            File directory = new File(FILE_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            objectMapper.writeValue(filePath.toFile(), bestsellers);
            log.info("베스트셀러 저장 완료: {}", filePath.toString());

        } catch (IOException e) {
            log.error("베스트셀러 저장 시 에러 발생", e);
        }
    }

    public List<Map<String, String>> loadBestsellerFromJson() {
        List<Map<String, String>> bestseller = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fileName = String.format(FILE_NAME, formattedDate);
        Path filePath = Paths.get(FILE_DIRECTORY, fileName);

        synchronized (lockObject) {
            try {
                if (Files.exists(filePath)) {
                    try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "rw");
                         FileChannel channel = raf.getChannel();
                         FileLock lock = channel.lock()) {
                        bestseller = objectMapper.readValue(filePath.toFile(), new TypeReference<List<Map<String, String>>>() {
                        });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    bestseller = fetchBestsellers();
                    saveBestsellersToFile(bestseller, filePath);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return bestseller;
        }
    }
}