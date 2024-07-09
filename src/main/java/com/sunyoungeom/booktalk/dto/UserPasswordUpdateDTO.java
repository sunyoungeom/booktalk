package com.sunyoungeom.booktalk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserPasswordUpdateDTO {
    // 현재 비밀번호
    private String currentPassword;
    // 새로운 비밀번호
    @NotBlank(message = "{user.password.notblank}")
    @Pattern(regexp = "^[a-z0-9]{1,10}$", message = "{user.password.pattern}")
    private String newPassword;
}
