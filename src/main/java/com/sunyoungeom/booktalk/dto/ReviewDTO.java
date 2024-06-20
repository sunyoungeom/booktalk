package com.sunyoungeom.booktalk.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ReviewDTO {
    private Long id;
    private String title;
    private String author;
    private String date;
    private String content;
    private int likes;
}
