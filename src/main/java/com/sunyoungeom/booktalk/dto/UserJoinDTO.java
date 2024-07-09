package com.sunyoungeom.booktalk.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class UserJoinDTO {
    // 닉네임
    @NotBlank(message = "{user.nickname.notblank}")
    @Size(max = 6, message = "{user.nickname.size}")
    private String nickname;
    // 이메일
    @Email(message = "{user.email.invalid}")
    @NotBlank(message = "{user.email.notblank}")
    private String email;
    // 비밀번호
    @NotBlank(message = "{user.password.notblank}")
    @Pattern(regexp = "^[a-z0-9]{1,10}$", message = "{user.password.pattern}")
    private String password;
}
