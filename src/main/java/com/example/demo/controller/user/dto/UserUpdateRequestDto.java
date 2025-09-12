package com.example.demo.controller.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserUpdateRequestDto {
//    @NotNull -> 수정용
    @Size(min = 1, max = 20)
    private String name;

//    @NotNull
    @Size(min = 1, max = 20)
    private String phoneNumber;

//    @NotNull
    private LocalDate birthDay;
}
