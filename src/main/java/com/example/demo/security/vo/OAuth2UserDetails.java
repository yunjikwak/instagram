package com.example.demo.security.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuth2UserDetails implements OAuth2User { // UserDetails + OAuth2User
    private UserDetails user; // 사용자의 아이디, 비밀번호, 권한(authorities) 정보
    private Map<String, Object> attributes;  // OAuth2User 소셜 서비스로부터 받은 추가 정보(attributes), 원본 사용자 정보(닉네임, 이메일 등)
    private Collection<? extends GrantedAuthority> authorities; // 사용자가 가진 권한 목록("ROLE_USER" 등)

    @Override
    public String getName() { // Spring Security -> 현재 로그인한 사용자를 식별하는 대표 ID를 반환
        // 권한 확인 시 socialId 사용 X username 사용
            // socialId는 자체로그인 사용자의 경우 null로 존재 -> 권한 확인 시마다 소셜인지 자체인지 구분해야함
            // 따라서 소셜로그인도 username을 저장 : kakao + {socialId} 으로 확인
        return user.getUsername();
    }

    public static OAuth2UserDetails create(OAuth2Resource resource, UserDetails user) {
        return new OAuth2UserDetails(user, resource.getAttributes(), user.getAuthorities());
    }

}
