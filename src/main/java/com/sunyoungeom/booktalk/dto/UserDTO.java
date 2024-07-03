package com.sunyoungeom.booktalk.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserDTO {
    private String profileImgPath;
    private String nickname;
    private String email;
    private String signUpType;
    private String signUpDate;
}
