package com.example.demo.controller.post.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentCreateRequestDto {
    @NotNull
    @Size(min = 1, max = 200)
    private String content;

    // dto로 받는 건 RESTful하지 X
//    @NotNull
//    private Integer postId;
//    @NotNull
//    private Integer userId;
}
