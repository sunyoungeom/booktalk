package com.sunyoungeom.booktalk.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReviewLikes {
    private Long id;
    private Long userId;
    private Long reviewId;

    public ReviewLikes(Long userId, Long reviewId) {
        this.userId = userId;
        this.reviewId = reviewId;
    }
}
