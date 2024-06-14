package com.sunyoungeom.booktalk.exception.common;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    HttpStatus getHttpStatus();
    int getCode();
    String getMessage();
}
