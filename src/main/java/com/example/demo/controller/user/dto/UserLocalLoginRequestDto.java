package com.example.demo.controller.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserLocalLoginRequestDto {
    @NotNull
    private String username;

    @NotNull
    private String password;
}
