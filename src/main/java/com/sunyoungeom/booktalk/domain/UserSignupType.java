package com.sunyoungeom.booktalk.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserSignupType {
    EMAIL("이메일"),
    GOOGLE("구글");

    private final String typeName;

}
