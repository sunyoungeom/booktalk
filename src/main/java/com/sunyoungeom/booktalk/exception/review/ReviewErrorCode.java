package com.sunyoungeom.booktalk.exception.review;

import com.sunyoungeom.booktalk.exception.common.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {
    REVIEW_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND,404,"존재하지 않는 리뷰입니다."),
    REVIEW_ALREADY_EXISTS_ERROR(HttpStatus.CONFLICT,409, "이미 작성한 리뷰입니다."),
    INACTIVE_USER_ERROR(HttpStatus.FORBIDDEN, 403,"권한이 없는 사용자입니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}

