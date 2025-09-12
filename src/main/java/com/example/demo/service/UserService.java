package com.example.demo.service;

import com.example.demo.common.exception.BaseException;
import com.example.demo.common.response.BaseResponseStatus;
import com.example.demo.controller.user.dto.*;
import com.example.demo.repository.user.UserRepository;
import com.example.demo.repository.user.entity.User;
import com.example.demo.security.JwtProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional(readOnly = true)
    public UserResponseDto findById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FIND_USER));
        return UserResponseDto.from(user);

    }

    @Transactional
    public UserResponseDto localSignUp(@Valid UserLocalCreateRequestDto request) {
        // 중복 확인
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BaseException(BaseResponseStatus.DUPLICATE_USERNAME);
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
//        // 중복 확인 -> 어차피 중복임! 이미 카카오 로그인 후 들어오는 URI라서
//        if (userRepository.existsByProviderAndSocialId (request.getProviderType(), request.getSocialId())) {
//            throw new BaseException(BaseResponseStatus.DUPLICATE_SOCIAL_ID);
//        }

        // 1. 임시 저장 유저 찾기
        User user = userRepository.findByProviderAndSocialId(request.getProviderType(), request.getSocialId())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FIND_USER));

        // 2. 가입 여부 확인
        if (user.getStatus() == User.UserStatus.ACTIVE) {
            throw new BaseException(BaseResponseStatus.DUPLICATE_SOCIAL_ID); // 이미 처리된 회원가입
        }

        // 3. 추가 정보 업데이트 -> ACTIVE 변경
        user.addSocialSignUp(
                request.getName(),
                request.getPhoneNumber(),
                request.getBirthDay()
        );

        // 정보 입력.. 이미
        // 정보 입력 중간만
            // .. 막아야함 이미 JWT 발행됨

        return UserResponseDto.from(user);
    }

    @Transactional
    public UserLoginResponseDto localLogin(@Valid UserLocalLoginRequestDto request) {
        // 아이디 확인
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.FAILED_TO_LOGIN));
        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }

        // 계정 상태 확인
        switch (user.getStatus()) {
            case BANNED:
            case DELETED, DORMANT:
                throw new BaseException(BaseResponseStatus.INACTIVE_USER);
            case NEEDS_TERMS_AGREEMENT:
                throw new BaseException(BaseResponseStatus.TERMS_NOT_AGREED);
            case ACTIVE:
                // check
                LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
                if (user.getTermsAgreedAt().isBefore(oneYearAgo)) {
                    user.requireTermsAgreement();
                    userRepository.save(user);
                    throw new BaseException(BaseResponseStatus.TERMS_NOT_AGREED);
                }
                break;
        }

        // Jwt 토큰 생성
        String accessToken = jwtProvider.createAccessToken(user.getId());
        return new UserLoginResponseDto(user.getId(), accessToken);
    }

    @Transactional
    public void agreeTerms(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FIND_USER));
        user.updateTermAgreement();
    }

    @Transactional
    public UserResponseDto update(Integer id, UserUpdateRequestDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FIND_USER));
        user.update(request.getName(), request.getPhoneNumber(),request.getBirthDay());

        return UserResponseDto.from(user);
    }

    @Transactional
    public void withdrawUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FIND_USER));

        if (user.getStatus() == User.UserStatus.DELETED) {
            throw new BaseException(BaseResponseStatus.USER_CONFLICT);
        }
        user.withdraw();
    }
}
