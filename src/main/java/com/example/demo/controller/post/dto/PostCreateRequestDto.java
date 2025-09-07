package com.example.demo.controller.post.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostCreateRequestDto {
    @NotNull
    @Size(min = 1, max = 200)
    private String content;
    private Integer userId;
}
