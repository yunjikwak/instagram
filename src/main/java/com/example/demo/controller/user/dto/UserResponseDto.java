package com.example.demo.controller.user.dto;

import com.example.demo.repository.user.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponseDto {
    private Integer id;
    private User.ProviderType providerType;
    private String username;
    private String socialId;
    private String name;
    private String phoneNumber;
    private LocalDate birthDay;
    private User.UserStatus status;
    private LocalDateTime termsAgreedAt;

    public static UserResponseDto from(User entity) {
        return new UserResponseDto(
                entity.getId(),
                entity.getProvider(),
                entity.getUsername(),
                entity.getSocialId(),
                entity.getName(),
                entity.getPhoneNumber(),
                entity.getBirthDay(),
                entity.getStatus(),
                entity.getTermsAgreedAt()
        );
    }
}
