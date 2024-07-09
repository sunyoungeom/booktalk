package com.sunyoungeom.booktalk.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserUpdateDTO {
    private Long id;
    private String profileImgPath;
    private String newNickname;
    private String currentPassword;
    private String newPassword;
}
