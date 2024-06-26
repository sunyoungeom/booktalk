package com.sunyoungeom.booktalk.exception;

import com.sunyoungeom.booktalk.exception.common.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {
    REVIEW_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND,404,"존재하지 않는 리뷰입니다."),
    REVIEW_ALREADY_EXISTS_ERROR(HttpStatus.CONFLICT,409, "이미 작성한 리뷰입니다."),
    REVIEW_BY_YOU_ERROR(HttpStatus.CONFLICT,409, "본인이 쓴 리뷰에는 좋아요를 누를 수 없습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}

