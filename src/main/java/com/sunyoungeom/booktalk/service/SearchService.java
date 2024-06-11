package com.sunyoungeom.booktalk.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public SearchService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Book> search(String query, Integer page) {
        List<Book> bookList = new ArrayList<>();
        try {
            String url = "https://dapi.kakao.com/v3/search/book";
            String apiKey = "9991c8dedd8f25708947e17ae3427876";
//            Integer page = 1;

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("query", query)
                    .queryParam("page", page);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + apiKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    builder.build().encode().toUri(),
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            String responseBody = response.getBody();
            log.info("API 응답: {}", responseBody);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode result = objectMapper.readTree(responseBody);
            JsonNode documents = result.path("documents");

            log.info(result.toString());
            log.info("document:{}", documents);

//            for (JsonNode document : documents) {
//            ArrayNode authorsNode = (ArrayNode) document.path("authors");
//            System.out.println(authorsNode.toString());
//// 문자열 배열 초기화
//            String[] authors = new String[authorsNode.size()];
//
//// JSON 배열을 문자열 배열로 변환
//            for (int i = 0; i < authorsNode.size(); i++) {
//                authors[i] = authorsNode.get(i).asText();
//                log.info("author: {}", authors[i]);
//            }
//
//            }


            for (JsonNode document : documents) {
                String title = document.path("title").asText();
//                String[] authors = document.path("authors").asArr();
                String date = document.path("datetime").asText();
                String imgPath = document.path("thumbnail").asText();
                String contents = document.path("contents").asText();


                StringBuilder stringBuilder = new StringBuilder(date);
                stringBuilder.delete(10, stringBuilder.length()); // 11번째 자리부터 끝까지 삭제합니다.
                String modifiedDate = stringBuilder.toString();




                ArrayNode authorsNode = (ArrayNode) document.path("authors");
                System.out.println(authorsNode.toString());
// 문자열 배열 초기화
//                String[] authors = new String[authorsNode.size()];



                // 작가 이름을 담을 StringBuilder 초기화
                StringBuilder authorBuilder = new StringBuilder();

// JSON 배열을 문자열로 결합
                for (int i = 0; i < authorsNode.size(); i++) {
                    String author = authorsNode.get(i).asText();
                    authorBuilder.append(author);

                    // 마지막 요소가 아닌 경우에는 쉼표를 추가
                    if (i < authorsNode.size() - 1) {
                        authorBuilder.append(", ");
                    }
                }

// 최종 작가 이름 문자열
                String authors = authorBuilder.toString();






                Book book = new Book();  // Assuming you have a proper Book class
                book.setTitle(title);
                book.setAuthor(authors);
                book.setContent(contents);
                book.setDate(modifiedDate);
                book.setImgPath(imgPath);
                bookList.add(book);
// JSON 배열

            }
//            for (Book book : bookList) {
//                log.info("book: {}", book.toString());
//            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return bookList;
    }

}
