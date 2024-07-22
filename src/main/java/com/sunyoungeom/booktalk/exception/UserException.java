package com.sunyoungeom.booktalk.exception;

import com.sunyoungeom.booktalk.exception.common.CommonErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final int code;


    public UserException(UserErrorCode errorCode) {
        super(errorCode.getMessage());
        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.getCode();
    }

    private int determineErrorCode(String message) {
        for (UserErrorCode errorCode : UserErrorCode.values()) {
            if (errorCode.getMessage().equals(message)) {
                return errorCode.getCode();
            }
        }
        return CommonErrorCode.INTERNAL_SERVER_ERROR.getCode();
    }
}
