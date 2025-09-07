package com.example.demo.security;

import com.example.demo.repository.user.entity.User;
import com.example.demo.security.vo.OAuth2UserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler { // 어디로 보낼지 결정
    public static final String REDIRECT_URI_PATH  = "/set-token";
    public static final String REDIRECT_URI = "http://localhost:8080" + REDIRECT_URI_PATH ;

    // JWT 발급을 위해 JwtTokenProvider를 주입
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) authentication;
        // 통일된 신분증OAuth2UserDetails ~> User 객체 꺼내기, 상세 프로필 확인용
        OAuth2UserDetails principal = (OAuth2UserDetails) authenticationToken.getPrincipal();
        User user = (User) principal.getUser();

        String targetUrl;

        if (user.getStatus() == User.UserStatus.NEEDS_TERMS_AGREEMENT) { // 신규 회원
            // 임시 상태 => 추가 정보 입력 필요
                // 리다이렉션 w socialId + provider (쿼리 파라미터)
            targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/signup/social")
                    .queryParam("provider", user.getProvider().name())
                    .queryParam("socialId", user.getSocialId())
                    .build().toUriString();

            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } else { // 기존 회원 - jwt 생성
//            String accessToken = jwtTokenProvider.createAccessToken(user.getId());
//            String refreshToken = jwtTokenProvider.createRefreshToken();

            // 토큰 생성
                // jwtProvider가 사용자 전체 인증 정보authenticationToken 받음
                // 사용자 고유 ID, 권한 등 핵심 정보 추출 -> 유효 기간 설정 후 비밀키JWT_SECRET로 서명
            String token = jwtProvider.generate(authenticationToken);
            // 로그인 전 사용자가 가려던 페이지 (어떻게 알지?)
                // RequestCache 덕분
                // 1. 사용자 -> 프라이빗 룸 시도
                // 2-1. 지배인Spring Security가 로그인 필요하다고 막음
                // 2-2. 지배인이 메모장RequestCache에 어디로 가려고 했음을 기록
                // 3. 로그인 페이지로 전송된 사용자가 다시 메모장을 보고 목적지로 안내됨
            String redirectUrl = JwtAuthorizationFilter.getRedirectUrl(request, response);

            // 위조 불가능하게 JWT 전달
            ResponseCookie cookie = ResponseCookie.from("accessToken", token)
                    .httpOnly(true) // JS 접근 X -> XSS 방지, 서버와 브라우저 사이의 통신 규약(HTTP)에 의해서만 전달 + JS 확인 못함
                    .secure(true) // HTTPS 연결 통해서만 쿠키 전송하도록 강제  -> 그럼 로컬에서 카카오 로그인 실행할 때도 https로 접속해야해??? 아까 안 해도 됐던 거 같은데 => http://localhost는 예외적으로 허용(천잰디)
                    .path("/") // 모든 경로에서 쿠키 사용 가능
                    .maxAge(60 * 60 * 24) // 유효 기간 1일
                    .sameSite("Strict") // 다른 사이트에서 쿠키 첨부 X, CSRF 공격 방어
                    .build();

            response.addHeader("Set-Cookie", cookie.toString()); // 쿠키 브라우저에게 전달
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        }
    }
}
