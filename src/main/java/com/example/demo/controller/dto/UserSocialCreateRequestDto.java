package com.example.demo.controller.dto;

import com.example.demo.repository.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class UserSocialCreateRequestDto {
    private User.ProviderType providerType;

    @NotNull
    private String socialId;

    @NotNull
    @Size(min = 1, max = 20)
    private String name;

    @NotNull
    @Size(min = 1, max = 20)
    private String phoneNumber;

    @NotNull
    private LocalDate birthDay;

    public static UserSocialCreateRequestDto from(User entity) {
        return new UserSocialCreateRequestDto(
                entity.getProvider(),
                entity.getSocialId(),
                entity.getName(),
                entity.getPhoneNumber(),
                entity.getBirthDay()
        );
    }
}
