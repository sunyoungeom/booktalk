package com.sunyoungeom.booktalk.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Review {
    private Long id;
    private String title;
    private String author;
    private String date;
    private String content;
    private int likes;

    public Review(String title, String author, String date, String content, int likes) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.content = content;
        this.likes = likes;
    }
}
