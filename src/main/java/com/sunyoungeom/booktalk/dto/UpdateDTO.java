package com.sunyoungeom.booktalk.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UpdateDTO {
    private String nickname;
    private String currentPassword;
    private String newPassword;
}
