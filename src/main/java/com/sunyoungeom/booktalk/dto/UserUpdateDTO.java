package com.sunyoungeom.booktalk.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserUpdateDTO {
    private String profileImgPath;
    private String newNickname;
    private String currentPassword;
    private String newPassword;
}
