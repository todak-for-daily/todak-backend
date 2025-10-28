package com.example.todak_server.controller;

import com.example.todak_server.entity.Member;
import com.example.todak_server.jwt.JwtTokenProvider;
import com.example.todak_server.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final JwtTokenProvider jwt;
    private final MemberRepository memberRepository;

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
        cookie.setSecure(false); // 로컬에선 fasle!
        cookie.setPath("/");
        cookie.setMaxAge(60*60*24*14);
        res.addCookie(cookie);

        return Map.of("accessToken",access);
    }

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
}
