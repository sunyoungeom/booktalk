package com.sunyoungeom.booktalk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserNicknameUpdateDTO {
    // 새로운 닉네임
    @NotBlank(message = "{user.nickname.notblank}")
    @Size(max = 6, message = "{user.nickname.size}")
    private String newNickname;
}
