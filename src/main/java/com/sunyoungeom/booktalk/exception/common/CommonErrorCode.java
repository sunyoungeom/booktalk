package com.sunyoungeom.booktalk.exception.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    ACCESS_DENIED_ERROR(HttpStatus.FORBIDDEN, 403,"해당 리소스에 접근할 수 있는 권한이 없습니다."),
    NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, 404, "요청하신 페이지를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
