package com.sunyoungeom.booktalk.exception;

import com.sunyoungeom.booktalk.exception.common.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, 404, "존재하지 않는 사용자입니다."),
    INVALID_PASSWORD_ERROR(HttpStatus.BAD_REQUEST, 400, "비밀번호를 다시 확인하세요."),
    NICKNAME_ALREADY_EXISTS_ERROR(HttpStatus.CONFLICT, 409, "사용중인 닉네임입니다."),
    EMAIL_ALREADY_EXISTS_ERROR(HttpStatus.CONFLICT, 409, "사용중인 이메일입니다."),
    IMMUTABLE_USER_FIELD(HttpStatus.BAD_REQUEST, 400, "해당 키는 업데이트 할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
