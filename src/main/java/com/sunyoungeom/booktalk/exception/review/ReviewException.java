package com.sunyoungeom.booktalk.exception.review;

import lombok.Getter;

@Getter
public class ReviewException extends RuntimeException {

    public ReviewException(String message) {
        super(message);
    }
}
