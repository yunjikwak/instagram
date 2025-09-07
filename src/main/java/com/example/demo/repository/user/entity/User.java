package com.example.demo.repository.user.entity;

import com.example.demo.repository.post.entity.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    public enum UserStatus {
        ACTIVE,
        DELETED,
        DORMANT,
        BANNED,
        NEEDS_TERMS_AGREEMENT
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
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user",
            cascade = CascadeType.ALL, // 부모 변화 -> 자식 변화
            orphanRemoval = true) // 부모 잃음 -> 제거
    private List<Post> posts;
    // 댓글
    // 좋아요
    // 구독
    // 결제 기록 <- 이것도 양방향?? 왜??
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
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                new ArrayList<>()
        );
    }

    public static User createInitialSocialUser(ProviderType provider, String socialId, String name) {
        String username = provider.name() + "_" + socialId;
        return new User(
                null,
                provider,
                socialId,
                username, // UNIQUE 제약조건을 위한 고유 값
                null,
                name,
                null,
                null,
                UserStatus.NEEDS_TERMS_AGREEMENT, // 임시 상태, 추가 정보 입력해야함
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                new ArrayList<>()
        );
    }

    public static User createSocialUser(ProviderType provider, String socialId, String name, String phoneNumber, LocalDate birthDay) {
        String username = provider.name() + "_" + socialId;
        return new User(
                null,
                provider,
                socialId,
                username,
                null,
                name,
                phoneNumber,
                birthDay,
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                new ArrayList<>()
        );
    }

    public void addSocialSignUp(String name, String phoneNumber, LocalDate birthDay) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthDay = birthDay;
        this.status = UserStatus.ACTIVE;
        this.termsAgreedAt = LocalDateTime.now();
    }

    public void updateTermAgreement() {
        this.status = UserStatus.ACTIVE;
        this.termsAgreedAt = LocalDateTime.now();
    }

    public void requireTermsAgreement() {
        this.status = UserStatus.NEEDS_TERMS_AGREEMENT;
    }

    public void withdraw() {
        this.status = UserStatus.DELETED;
    }

}
