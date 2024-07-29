package com.sunyoungeom.booktalk.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class User {
    private Long id;
    private String profileImgPath;
    private String nickname;
    private String email;
    private String password;
    private String signUpType;
    private String signUpDate;
    private UserRole userRole;

    public User(String nickname, String email, String password) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;

    }
}
