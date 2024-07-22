package com.sunyoungeom.booktalk.exception;

import com.sunyoungeom.booktalk.exception.common.CommonErrorCode;
import com.sunyoungeom.booktalk.exception.common.ErrorCode;
import com.sunyoungeom.booktalk.exception.common.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

//    @ExceptionHandler(ReviewException.class)
//    public ResponseEntity<ErrorResponse> handleReviewException(ReviewException ex) {
//        ErrorCode errorCode = determineErrorCode(ex.getMessage(), "review");
////        ErrorResponse errorResponse = makeErrorResponse(errorCode);
//        log.warn("handleReviewException: code = {}, message = {}", errorCode.getCode(), ex.getMessage());
//        return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
//    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserException(UserException ex) {
        ErrorResponse errorResponse = makeErrorResponse(ex);
        log.warn("handleUserException: code = {}, message = {}", ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    private ErrorCode determineErrorCode(String message, String ex) {
        if (ex.equals("review")) {
            for (ReviewErrorCode errorCode : ReviewErrorCode.values()) {
                if (errorCode.getMessage().equals(message)) {
                    return errorCode;
                }
            }
        }
        if (ex.equals("user")) {
            for (UserErrorCode errorCode : UserErrorCode.values()) {
                if (errorCode.getMessage().equals(message)) {
                    return errorCode;
                }
            }
        }

        for (CommonErrorCode errorCode : CommonErrorCode.values()) {
            if (errorCode.getMessage().equals(message)) {
                return errorCode;
            }
        }

        return CommonErrorCode.INTERNAL_SERVER_ERROR;
    }

    private static ErrorResponse makeErrorResponse(UserException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(e.getCode())
                .message(e.getMessage())
                .build();
        return errorResponse;
    }
}
