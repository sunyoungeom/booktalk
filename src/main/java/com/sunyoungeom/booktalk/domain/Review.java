package com.sunyoungeom.booktalk.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Review {
    private Long id;
    private Long userId;
    private String title;
    private String author;
    private String date;
    private String content;
    private int likes;

    public Review(Long userId, String title, String author, String content) {
        this.userId = userId;
        this.title = title;
        this.author = author;
        this.content = content;
    }
}
