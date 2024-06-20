package com.sunyoungeom.booktalk.dto;

import com.sunyoungeom.booktalk.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String profileImgPath;
    private String nickname;
    private String email;
    private String signUpType;
    private LocalDate signUpDate;
}
