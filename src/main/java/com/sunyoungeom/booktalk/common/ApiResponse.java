package com.sunyoungeom.booktalk.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private Status status;
    private HttpStatus httpStatus;
    private String message;
    private T data;
}