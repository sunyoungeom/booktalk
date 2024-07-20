package com.sunyoungeom.booktalk.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.sunyoungeom.booktalk.domain.Book;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SearchService {

    private final RestTemplate restTemplate;

    @Value("${api.key.kakao-search}")
    private String apiKey;

    @Autowired
    public SearchService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Book> search(String query, Integer page) {
        List<Book> bookList = new ArrayList<>();
        try {
            String url = "https://dapi.kakao.com/v3/search/book";

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("query", query)
                    .queryParam("page", page);

            // 요청 헤더에 키값 추가
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + apiKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 응답 결과
            ResponseEntity<String> response = restTemplate.exchange(
                    builder.build().encode().toUri(),
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            String responseBody = response.getBody();

            // Json 형식으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode result = objectMapper.readTree(responseBody);
            JsonNode documents = result.path("documents");

            // 검색 결과
            for (JsonNode document : documents) {
                String title = document.path("title").asText();
                // 제목 마지막 공백 제거
                title = title.replaceAll("\\s+$", "");
                String date = document.path("datetime").asText();
                String imgPath = document.path("thumbnail").asText();
                String contents = document.path("contents").asText();

                // 출간일 형식: XXXX-XX-XX으로 변경
                StringBuilder stringBuilder = new StringBuilder(date);
                stringBuilder.delete(10, stringBuilder.length());
                String modifiedDate = stringBuilder.toString();

                // 저자 부분
                ArrayNode authorsNode = (ArrayNode) document.path("authors");
                StringBuilder authorBuilder = new StringBuilder();

                // 복수 저자의 경우 중간에 쉼표 추가
                for (int i = 0; i < authorsNode.size(); i++) {
                    String author = authorsNode.get(i).asText();
                    authorBuilder.append(author);

                    if (i < authorsNode.size() - 1) {
                        authorBuilder.append(", ");
                    }
                }
                String authors = authorBuilder.toString();

                // 책 객체로 변환 후 리스트에 추가
                Book book = new Book();
                book.setTitle(title);
                book.setAuthor(authors);
                book.setContent(contents);
                book.setDate(modifiedDate);
                book.setImgPath(imgPath);
                bookList.add(book);

                log.info("title: {}, author: {}, date: {}, content: {}, imgPath: {}", title, authors, date, contents, imgPath);
            }

            return bookList;
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
