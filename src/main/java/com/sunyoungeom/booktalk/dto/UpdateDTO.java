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
    private String newNickname;
    private String currentPassword;
    private String newPassword;
}
