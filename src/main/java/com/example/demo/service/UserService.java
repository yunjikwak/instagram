package com.example.demo.service;

import com.example.demo.controller.dto.UserLocalCreateRequestDto;
import com.example.demo.controller.dto.UserResponseDto;
import com.example.demo.controller.dto.UserSocialCreateRequestDto;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserResponseDto findById(Integer id) {
        return null;
    }

    public UserResponseDto localSignUp(@Valid UserLocalCreateRequestDto request) {
        // 중복 확인
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디");
        }

        // 암호화
        String hash = passwordEncoder.encode(request.getPassword());

        User user = User.createLocalUser(
                request.getUsername(),
                hash,
                request.getName(),
                request.getPhoneNumber(),
                request.getBirthDay()
        );
        User created = userRepository.save(user);
        return UserResponseDto.from(created);
    }

    public UserResponseDto socialSignUp(@Valid UserSocialCreateRequestDto request) {
        // 중복 확인
        if (userRepository.existsByProviderAndSocialId (request.getProviderType(), request.getSocialId())) {
            throw new IllegalArgumentException("이미 사용 중인 소셜 아이디");
        }

        User user = User.createSocialUser(
                request.getProviderType(),
                request.getSocialId(),
                request.getName(),
                request.getPhoneNumber(),
                request.getBirthDay()
        );
        User created = userRepository.save(user);
        return UserResponseDto.from(created);
    }
}
