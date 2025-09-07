package com.example.demo.security.vo;

import com.example.demo.repository.user.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuth2KakaoResource implements OAuth2Resource {
    public User.ProviderType providerType;
    public String socialId;
    public String name;
    public Map<String, Object> attributes;
    public Map<String, Object> account;
    public Map<String, Object> profile;
    public String email;

    public static OAuth2KakaoResource create(OAuth2User resourceResponse) {
        final Map<String, Object> attributes = resourceResponse.getAttributes();
        final Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        final Map<String, Object> profile = account != null ? (Map<String, Object>) account.get("profile") : null;

        return new OAuth2KakaoResource(
                User.ProviderType.KAKAO,
                String.valueOf(attributes.get("id")),
                profile != null ? (String) profile.get("nickname") : null,
                attributes,
                account,
                profile,
                account != null ? (String) account.get("email") : null
        );
    }

}
