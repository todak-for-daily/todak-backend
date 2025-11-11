package com.example.todak_server.controller;

import com.example.todak_server.entity.Member;
import com.example.todak_server.jwt.JwtTokenProvider;
import com.example.todak_server.repository.MemberRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Tag(name = "토큰 관련 API", description = "액세스/리프레시 토큰 관련")
@RestController
@RequiredArgsConstructor
public class TokenController {

    private final JwtTokenProvider jwt;
    private final MemberRepository memberRepository;

    @Operation(summary = "액세스 토큰 첫 발급", description = "로그인 시 액세스 토큰 발급해줌. 서버 내에서 자동 실행됨.")
    @GetMapping("/token")
    public Map<String, String> issue(
            @AuthenticationPrincipal OAuth2User principal,
            OAuth2AuthenticationToken oauth2Auth,
            HttpServletResponse res
    ) {
        String provider   = oauth2Auth.getAuthorizedClientRegistrationId().toUpperCase(); // "GOOGLE"
        String providerId = principal.getAttribute("sub");

        Member m = memberRepository.findByProviderAndProviderId(provider, providerId).orElseThrow();

        String subject = provider + ":"+providerId;
        String access = jwt.createAccess(subject, m.getId(), List.of("ROLE_USER"));
        String refresh = jwt.createRefresh(subject, m.getId());

        Cookie cookie = new Cookie("refresh_token", refresh);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // 로컬에선 fasle! 운영이면 true.
        cookie.setPath("/");
        cookie.setMaxAge(60*60*24*14);
        res.addCookie(cookie);

        return Map.of("accessToken",access);
    }

    @Operation(summary = "액세스 토큰 재발급", description = "쿠키의 리프레시 토큰을 이용해 만료된 액세스 토큰을 재발급받기.")
    //Refresh -> Access 교환 (프론트는 credentials: 'include'로 호출
    @PostMapping("/api/auth/token")
    public Map<String, String> reissue(@CookieValue(name="refresh_token", required = false)String refresh) {
        if(refresh == null || !jwt.validate(refresh)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid refresh token");
        }
        String subject = jwt.getSubject(refresh);
        Long mid = jwt.getMemberId(refresh);
        String access = jwt.createAccess(subject, mid, List.of("ROLE_USER"));
        return Map.of("accessToken",access);
    }

    @Operation(summary = "로그아웃", description = "리프레시 토큰 쿠키 없애기. 프론트에서는 액세스 토큰 지워야 함.")
    @PostMapping("/api/auth/logout")
    public Map<String, String> logout(HttpServletResponse res) {
        // 리프레시 토큰 쿠키 삭제
        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(false)     // 운영 환경이면 true
                .sameSite("None")  // 크로스도메인일 경우 필수
                .build();

        res.addHeader("Set-Cookie", deleteCookie.toString());
        return Map.of("message", "logout success");
    }
}
