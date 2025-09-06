package com.example.demo.security;

import com.example.demo.repository.UserRepository;
import com.example.demo.repository.entity.User;
import com.example.demo.security.vo.OAuth2KakaoResource;
import com.example.demo.security.vo.OAuth2Resource;
import com.example.demo.security.vo.OAuth2UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    // provider 별로 vo 생성
    // resourceResponse ~> 표준화된 양식OAuth2Resource 으로 정리
    private OAuth2Resource extract(String provider, OAuth2User resourceResponse) {
        return switch (provider.toLowerCase()) {
            case "kakao" -> OAuth2KakaoResource.create(resourceResponse);
            default -> throw new IllegalArgumentException("존재하지 않는 RegistrationId : " + provider);
        };
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest resourceRequest) throws OAuth2AuthenticationException {
        // 카카오 서버 ~> 사용자 정보 받아오기
            // Map 형태의 원본 데이터(raw data)
        final OAuth2User resourceResponse = super.loadUser(resourceRequest);
        String providerName = resourceRequest.getClientRegistration().getRegistrationId();

        OAuth2Resource resource = extract(providerName, resourceResponse);

        // 카카오 로그인 + 정보 입력 받아야 회원가입 완료됨
        User.ProviderType providerType = resource.getProviderType();
        String socialId = resource.getSocialId();

        // 사용자 조회 - providerType + socialId 동일한 사용자 존재 여부 확인
        Optional<User> userOptional = userRepository.findByProviderAndSocialId(providerType, socialId);

        User user;
        if (userOptional.isPresent()) { // 이미 사용자 존재 -> 가입되어있음
            user = userOptional.get(); // db에서 꺼낸 값으로 확정
        } else { // 신규회원
            user = User.createInitialSocialUser( // 임시 회원 생성
                    providerType,
                    socialId,
                    resource.getName() // kakao nickname
            );
            userRepository.save(user);
        }

        // OAuth2UserDetails로 래핑
            // spring security 표준화된 OAuth2User 형식으로 포장 -> OAuth2UserDetails
        return OAuth2UserDetails.create(resource, user);
    }
}
