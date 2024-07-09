package com.sunyoungeom.booktalk.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserProfileImageUpdateDTO {
    // 새로운 이미지 경로
    @NotNull(message = "{user.profileImage.notnull}")
    private String profileImgPath;
}
