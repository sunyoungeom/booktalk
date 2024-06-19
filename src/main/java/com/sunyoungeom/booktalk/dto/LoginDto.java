package com.sunyoungeom.booktalk.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginDto {
    private String email;
    private String password;
}
