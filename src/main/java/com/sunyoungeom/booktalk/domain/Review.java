package com.sunyoungeom.booktalk.domain;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
public class Review {
    private Long id;
    private Long userId;
    private String title;
    private String author;
    private String date;
    private String content;
    private int likes;
}
