package com.example.demo.repository.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    public enum UserStatus {
        ACTIVATE,
        DELETED,
        DORMANT,
        BANNED
    }
    public enum ProviderType {
        LOCAL,
        KAKAO,
        NAVER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private ProviderType provider;

    private String socialId; // 소셜 로그인이라면

    @Column(unique = true)
    private String username; // 로컬 로그인이라면, 소문자 영어/숫자/_/. 만 포함, 1~20자

    private String password;
    private String name;
    private String phoneNumber;
    private LocalDate birthDay;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime termsAgreedAt;

    // 양방향이어야 하는 것들 고르기
    // 게시글
    // 댓글
    // 좋아요
    // 구독
    // 결제 기록
    // 신고

    public static User createLocalUser(String username, String password, String name, String phoneNumber, LocalDate birthDay) {
        return new User(
                null,
                ProviderType.LOCAL,
                null,
                username,
                password,
                name,
                phoneNumber,
                birthDay,
                UserStatus.ACTIVATE,
                LocalDateTime.now(),
                null,
                LocalDateTime.now()
        );
    }

    public static User createSocialUser(ProviderType provider, String socialId, String name, String phoneNumber, LocalDate birthDay) {
        return new User(
                null,
                provider,
                socialId,
                null,
                null,
                name,
                phoneNumber,
                birthDay,
                UserStatus.ACTIVATE,
                LocalDateTime.now(),
                null,
                LocalDateTime.now()
        );
    }

}
