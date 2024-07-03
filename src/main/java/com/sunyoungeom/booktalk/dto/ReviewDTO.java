package com.sunyoungeom.booktalk.dto;

import com.sunyoungeom.booktalk.domain.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ReviewDTO {
    private Long id;
    private Long userId;
    private String title;
    private String author;
    private String date;
    private String content;
    private int likes;
    private boolean liked;
    private User user;
}
