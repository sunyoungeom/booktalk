package com.sunyoungeom.booktalk.exception;

import com.sunyoungeom.booktalk.exception.common.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ReviewException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final int code;

    public ReviewException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.getCode();
    }
}
