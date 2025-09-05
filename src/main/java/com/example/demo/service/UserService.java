package com.example.demo.service;

import com.example.demo.controller.dto.UserLocalCreateRequestDto;
import com.example.demo.controller.dto.UserLocalLoginRequestDto;
import com.example.demo.controller.dto.UserResponseDto;
import com.example.demo.controller.dto.UserSocialCreateRequestDto;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponseDto findById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id"));
        return UserResponseDto.from(user);

    }

    @Transactional
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

    @Transactional
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

    public UserResponseDto localLogin(@Valid UserLocalLoginRequestDto request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return UserResponseDto.from(user);
    }
}
