package com.sunyoungeom.booktalk.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReviewLikesDTO {
    private boolean isLiked;
    private int likes;
}