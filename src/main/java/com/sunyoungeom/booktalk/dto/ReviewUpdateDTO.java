package com.sunyoungeom.booktalk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReviewUpdateDTO {
    // 작성자 아이디
    @NotNull(message = "{review.userid.notnull}")
    private Long userId;
    // 내용
    @NotBlank(message = "{review.content.notblank}")
    @Size(max = 500, message = "{review.content.size}")
    private String content;
}

