package com.sunyoungeom.booktalk.exception;

import lombok.Getter;

@Getter
public class ReviewException extends RuntimeException {

    public ReviewException(String message) {
        super(message);
    }
}
