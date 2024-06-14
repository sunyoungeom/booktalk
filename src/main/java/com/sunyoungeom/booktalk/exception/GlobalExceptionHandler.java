package com.sunyoungeom.booktalk.exception;

import com.sunyoungeom.booktalk.exception.common.CommonErrorCode;
import com.sunyoungeom.booktalk.exception.common.ErrorCode;
import com.sunyoungeom.booktalk.exception.common.ErrorResponse;
import com.sunyoungeom.booktalk.exception.review.ReviewException;
import com.sunyoungeom.booktalk.exception.review.ReviewErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleAllException(Exception ex) {
        ErrorCode errorCode = determineErrorCode(ex.getMessage());
        ErrorResponse errorResponse = makeErrorResponse(errorCode);
        log.warn("handleAllException: code = {}, message = {}", errorCode.getCode(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
    }

    @ExceptionHandler(ReviewException.class)
    public ResponseEntity<ErrorResponse> handleReviewException(ReviewException ex) {
        ErrorCode errorCode = determineErrorCode(ex.getMessage());
        ErrorResponse errorResponse = makeErrorResponse(errorCode);
        log.warn("handleReviewException: code = {}, message = {}", errorCode.getCode(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
    }


    private ErrorCode determineErrorCode(String message) {
        for (ReviewErrorCode errorCode : ReviewErrorCode.values()) {
            if (errorCode.getMessage().equals(message)) {
                return errorCode;
            }
        }

        for (CommonErrorCode errorCode : CommonErrorCode.values()) {
            if (errorCode.getMessage().equals(message)) {
                return errorCode;
            }
        }

        return CommonErrorCode.INTERNAL_SERVER_ERROR;
    }

    private static ErrorResponse makeErrorResponse(ErrorCode errorCode) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        return errorResponse;
    }
}
