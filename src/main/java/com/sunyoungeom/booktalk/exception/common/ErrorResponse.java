package com.sunyoungeom.booktalk.exception.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

    private final int code;
    private final String message;
}
