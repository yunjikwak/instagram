package com.example.demo.controller.dto;

import com.example.demo.repository.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class UserLocalCreateRequestDto {
    @NotNull
    @Size(min = 1, max = 20)
    private String username;

    @NotNull
    @Size(min = 6, max = 20)
    private String password;

    @NotNull
    @Size(min = 1, max = 20)
    private String name;

    @NotNull
    @Size(min = 1, max = 20)
    private String phoneNumber;

    @NotNull
    private LocalDate birthDay;

    public static UserLocalCreateRequestDto from(User entity) {
        return new UserLocalCreateRequestDto(
                entity.getUsername(),
                entity.getPassword(),
                entity.getName(),
                entity.getPhoneNumber(),
                entity.getBirthDay()
        );
    }

}
