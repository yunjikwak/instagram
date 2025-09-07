package com.example.demo.security.vo;

import com.example.demo.repository.user.entity.User;

import java.util.Map;

public interface OAuth2Resource { // 뽑아낼 정보 정리
    // 출처, 식별Id, 이름,
    // 원본 사용자 데이터(이름, 이메일, 프로필 이미지 등) - getAttributes
    // 이메일, 프로필, 연령대, 성별 등 - getAccount
    // 닉네임, 프로필 이미지 URL 등 - getProfile

    User.ProviderType getProviderType();
    String getSocialId();
    String getName();
    Map<String, Object> getAttributes();
//    Map<String, Object> getAccount();
//    Map<String, Object> getProfile();
//    String getEmail();
}
