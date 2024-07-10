package com.sunyoungeom.booktalk.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;

public class ApiResponseUtil {
    public static ResponseEntity<Object> createResponse(Status status, HttpStatus httpStatus, String message, Object data) {
        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .status(status)
                .httpStatus(httpStatus)
                .message(message)
                .data(data)
                .build();

        return ResponseEntity.status(httpStatus).body(response);
    }

    public static ResponseEntity<Object> successResponse(HttpStatus httpStatus, String message, Object data) {
        return createResponse(Status.SUCCESS, httpStatus, message, data);
    }

    public static ResponseEntity<Object> failResponse(HttpStatus httpStatus, String message) {
        return createResponse(Status.FAIL, httpStatus, message, null);
    }

    public static ResponseEntity<Object> errorResponse(HttpStatus httpStatus, String message) {
        return createResponse(Status.ERROR, httpStatus, message, null);
    }

    public static ResponseEntity<Object> validatedErrorResponse(String message, BindingResult bindingResult) {
        List<String> errorMessages = bindingResult.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());

        return createResponse(Status.ERROR, HttpStatus.BAD_REQUEST, message, errorMessages);
    }
}