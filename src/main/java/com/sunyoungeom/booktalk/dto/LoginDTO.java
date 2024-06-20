package com.sunyoungeom.booktalk.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginDTO {
    private String email;
    private String password;
}
