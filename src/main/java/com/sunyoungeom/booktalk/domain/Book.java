package com.sunyoungeom.booktalk.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Book {
    private Long id;
    private String title;
    private String category;
    private String author;
    private String date;
    private String content;
    private String imgPath;

    public Book(String title, String category, String author, String date, String content, String imgPath) {
        this.title = title;
        this.category = category;
        this.author = author;
        this.date = date;
        this.content = content;
        this.imgPath = imgPath;
    }
}
